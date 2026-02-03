package com.example.UberComp.service;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.dto.ride.CreateRideDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.dto.vehicle.VehicleLocationDTO;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class DriverService {
    private final DriverRepository driverRepository;
    private final DriverChangeRequestRepository changeRequestRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final AccountRepository accountRepository;
    private final VehicleRepository vehicleRepository;
    private final ObjectMapper objectMapper;
    private final RideRepository rideRepository;
    private final ScheduledRideRepository scheduledRideRepository;
    private final CoordinateRepository coordinateRepository;
    private final String mapboxApiKey;

    private static final int MAX_UPTIME_MINUTES = 480;
    private static final int UPTIME_TOLERANCE_MINUTES = 10;
    private static final int MAX_SCHEDULE_HOURS = 5;
    private static final double MAX_DISTANCE_KM = 5.0;
    private static final long MAX_PICKUP_TIME_MINUTES = 10;

    private final Map<String, CachedTravelTime> travelTimeCache = new ConcurrentHashMap<>();
    private static final long TRAVEL_TIME_CACHE_TTL_MS = 15 * 60 * 1000;

    @Autowired
    public DriverService(
            DriverRepository driverRepository,
            DriverChangeRequestRepository changeRequestRepository,
            VehicleTypeRepository vehicleTypeRepository,
            AccountRepository accountRepository,
            VehicleRepository vehicleRepository,
            ObjectMapper objectMapper,
            RideRepository rideRepository,
            ScheduledRideRepository scheduledRideRepository,
            CoordinateRepository coordinateRepository,
            @Value("${mapbox.api.key:YOUR_DEFAULT_KEY}") String mapboxApiKey) {
        this.driverRepository = driverRepository;
        this.changeRequestRepository = changeRequestRepository;
        this.vehicleTypeRepository = vehicleTypeRepository;
        this.accountRepository = accountRepository;
        this.vehicleRepository = vehicleRepository;
        this.objectMapper = objectMapper;
        this.rideRepository = rideRepository;
        this.scheduledRideRepository = scheduledRideRepository;
        this.coordinateRepository = coordinateRepository;
        this.mapboxApiKey = mapboxApiKey;
    }

    public List<GetVehiclePositionDTO> getVehiclePositions() {
        return java.util.List.of();
    }

    public DriverStatus getStatus(Long id) {
        Optional<Driver> driver = driverRepository.findById(id);
        if (driver.isEmpty()) return null;
        return driver.get().getStatus();
    }

    @Transactional
    public DriverDTO register(CreateDriverDTO dto, User user) {
        if (vehicleRepository.findByPlate(dto.getVehicleDTO().getPlate()) != null) {
            throw new RuntimeException("License plate already registered");
        }

        if (accountRepository.findByEmail(dto.getAccountDTO().getEmail()) == null) {
            throw new RuntimeException("User not found after account creation");
        }

        Driver driver = (Driver) user;
        driver.setUptime(0);
        driver.setStatus(DriverStatus.OFFLINE);

        driver = driverRepository.save(driver);

        Vehicle vehicle = new Vehicle();
        vehicle.setModel(dto.getVehicleDTO().getModel());
        vehicle.setPlate(dto.getVehicleDTO().getPlate());
        vehicle.setSeatNumber(dto.getVehicleDTO().getSeatNumber());
        vehicle.setBabySeat(dto.getVehicleDTO().getBabySeat());
        vehicle.setPetFriendly(dto.getVehicleDTO().getPetFriendly());

        VehicleType vehicleType = vehicleTypeRepository
                .findVehicleTypeByName(dto.getVehicleDTO().getVehicleTypeDTO().getName());
        if (vehicleType != null) {
            vehicle.setVehicleType(vehicleType);
        } else {
            vehicleType = new VehicleType();
            vehicleType.setName(dto.getVehicleDTO().getVehicleTypeDTO().getName());
            vehicleType.setPrice(0.0);
            vehicleType = vehicleTypeRepository.save(vehicleType);
            vehicle.setVehicleType(vehicleType);
        }

        vehicle.setDriver(driver);
        vehicle = vehicleRepository.save(vehicle);

        driver.setVehicle(vehicle);
        driver = driverRepository.save(driver);

        return new DriverDTO(
                new AccountDTO(driver.getAccount().getEmail()),
                new CreatedUserDTO(driver),
                new VehicleDTO(vehicle),
                driver.getUptime(),
                false,
                null
        );
    }

    public DriverDTO findByUserId(Long userId) {
        Driver driver = driverRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found with user id: " + userId));

        Account account = driver.getAccount();
        Vehicle vehicle = driver.getVehicle();

        boolean blocked = account.getAccountStatus().equals(AccountStatus.BLOCKED);

        return new DriverDTO(
                new AccountDTO(account.getEmail()),
                new CreatedUserDTO(driver),
                new VehicleDTO(vehicle),
                driver.getUptime(),
                blocked,
                account.getBlockingReason()
        );
    }

    public DriverDTO findByAccountId(Long userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found with user id: " + userId));
        Driver driver = (Driver) account.getUser();

        boolean blocked = account.getAccountStatus().equals(AccountStatus.BLOCKED);

        Vehicle vehicle = driver.getVehicle();

        return new DriverDTO(
                new AccountDTO(account.getEmail()),
                new CreatedUserDTO(driver),
                new VehicleDTO(vehicle),
                driver.getUptime(),
                blocked,
                account.getBlockingReason()
        );
    }

    private Coordinate saveOrGetCoordinate(Coordinate coordinate) {
        if (coordinate == null) {
            return null;
        }

        if (coordinate.getId() != null) {
            return coordinate;
        }

        if (coordinate.getAddress() != null && !coordinate.getAddress().trim().isEmpty()) {
            String address = coordinate.getAddress();
            if (!address.toLowerCase().contains("novi sad")) {
                address = address + ", Novi Sad, Serbia";
                coordinate.setAddress(address);
            }

            Optional<Coordinate> existingByAddress = coordinateRepository.findByAddress(address);
            if (existingByAddress.isPresent()) {
                return existingByAddress.get();
            }
        }

        if (coordinate.getLat() != null && coordinate.getLon() != null) {
            Optional<Coordinate> existing = coordinateRepository.findByLatAndLon(
                    coordinate.getLat(),
                    coordinate.getLon()
            );

            if (existing.isPresent()) {
                return existing.get();
            }
        }

        try {
            return coordinateRepository.save(coordinate);
        } catch (Exception e) {
            if (coordinate.getAddress() != null) {
                Optional<Coordinate> byAddress = coordinateRepository.findByAddress(coordinate.getAddress());
                if (byAddress.isPresent()) {
                    return byAddress.get();
                }
            }

            if (coordinate.getLat() != null && coordinate.getLon() != null) {
                Optional<Coordinate> byLatLon = coordinateRepository.findByLatAndLon(
                        coordinate.getLat(),
                        coordinate.getLon()
                );
                if (byLatLon.isPresent()) {
                    return byLatLon.get();
                }
            }
            throw new RuntimeException("Failed to save coordinate: " + e.getMessage());
        }
    }

    @Transactional
    public void submitDriverChangeRequest(Long driverId, UpdateDriverDTO changeRequest) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        DriverChangeRequest request = new DriverChangeRequest();
        request.setDriver(driver);

        try {
            request.setRequestedChanges(objectMapper.writeValueAsString(changeRequest));
        } catch (Exception e) {
            throw new RuntimeException("JSON serialization failed", e);
        }

        request.setStatus("PENDING");
        request.setRequestDate(LocalDateTime.now());
        changeRequestRepository.save(request);
    }

    public AvailableDriverDTO getAvailableDriver(CreateRideDTO rideDTO) {
        LocalDateTime scheduledTime = rideDTO.getScheduled();

        if (scheduledTime != null &&
                scheduledTime.isAfter(LocalDateTime.now().plusHours(MAX_SCHEDULE_HOURS))) {
            return null;
        }

        Coordinate pickupLocation = geocodeAddressWithCache(rideDTO.getStartAddress().getAddress());
        Coordinate dropoffLocation = geocodeAddressWithCache(rideDTO.getDestinationAddress().getAddress());

        if (pickupLocation == null || dropoffLocation == null) {
            return null;
        }

        List<Driver> onlineDrivers = findByStatus(DriverStatus.ONLINE, rideDTO.getVehicleType(),
                rideDTO.getBabySeat(), rideDTO.getPetFriendly());
        List<Driver> drivingDrivers = findByStatus(DriverStatus.DRIVING, rideDTO.getVehicleType(),
                rideDTO.getBabySeat(), rideDTO.getPetFriendly());

        if (onlineDrivers.isEmpty() && drivingDrivers.isEmpty()) {
            return null;
        }

        List<DriverWithLocation> candidateDrivers = new ArrayList<>();

        for (Driver driver : onlineDrivers) {
            if (driver.getAccount().getAccountStatus().equals(AccountStatus.BLOCKED)) continue;
            Coordinate location = getDriverLocation(driver);

            if (location != null) {
                double distance = calculateHaversineDistance(location, pickupLocation);

                if (distance <= MAX_DISTANCE_KM) {
                    candidateDrivers.add(new DriverWithLocation(driver, location, DriverStatus.ONLINE));
                }
            }
        }

        for (Driver driver : drivingDrivers) {
            if (driver.getAccount().getAccountStatus().equals(AccountStatus.BLOCKED)) continue;
            Coordinate location = getDriverLocation(driver);
            if (location != null) {
                double distance = calculateHaversineDistance(location, pickupLocation);

                if (distance <= MAX_DISTANCE_KM) {
                    candidateDrivers.add(new DriverWithLocation(driver, location, DriverStatus.DRIVING));
                }
            }
        }

        if (candidateDrivers.isEmpty()) {
            return null;
        }

        List<Coordinate> locations = candidateDrivers.stream()
                .map(dwl -> dwl.location)
                .collect(Collectors.toList());

        Map<String, Long> travelTimesToPickup = calculateMultipleTravelTimes(pickupLocation, locations);

        long rideDuration = calculateTravelTimeWithCache(pickupLocation, dropoffLocation);

        if (rideDuration < 0) {
            return null;
        }

        DriverWithAvailabilityAndLocation bestDriverInfo = findBestDriver(
                candidateDrivers,
                pickupLocation,
                dropoffLocation,
                scheduledTime,
                travelTimesToPickup,
                rideDuration
        );

        if (bestDriverInfo == null) return null;

        Driver bestDriver = bestDriverInfo.driver;
        Account account = bestDriver.getAccount();
        Vehicle vehicle = bestDriver.getVehicle();

        DriverDTO driverDTO = new DriverDTO(
                new AccountDTO(account.getEmail()),
                new CreatedUserDTO(bestDriver),
                new VehicleDTO(vehicle),
                bestDriver.getUptime(),
                false,
                null
        );

        Coordinate vehicleLocation = bestDriverInfo.location;
        String locationAddress = getAddressFromCoordinate(vehicleLocation);

        VehicleLocationDTO vehicleLocationDTO = new VehicleLocationDTO(
                vehicleLocation.getLat(),
                vehicleLocation.getLon(),
                locationAddress
        );

        if (scheduledTime != null)
            return new AvailableDriverDTO(
                    driverDTO,
                    0L,
                    vehicleLocationDTO,
                    scheduledTime.minusMinutes(bestDriverInfo.estimatedPickupMinutes)
            );
        if (bestDriverInfo.estimatedPickupMinutes > MAX_PICKUP_TIME_MINUTES) return null;
        return new AvailableDriverDTO(
                driverDTO,
                bestDriverInfo.estimatedPickupMinutes,
                vehicleLocationDTO,
                null
        );
    }

    private DriverWithAvailabilityAndLocation findBestDriver(
            List<DriverWithLocation> candidates,
            Coordinate pickupLocation,
            Coordinate dropoffLocation,
            LocalDateTime scheduledTime,
            Map<String, Long> preCalculatedTimes,
            long rideDuration) {

        Map<Driver, DriverAvailabilityInfo> availableDrivers = new HashMap<>();

        for (DriverWithLocation dwl : candidates) {
            String key = formatCoordinateKey(pickupLocation, dwl.location);
            Long timeToPickup = preCalculatedTimes.get(key);

            if (timeToPickup == null || timeToPickup < 0) {
                continue;
            }

            if (timeToPickup > MAX_PICKUP_TIME_MINUTES && scheduledTime == null) {
                continue;
            }

            DriverAvailability availability;

            if (dwl.status == DriverStatus.ONLINE) {
                availability = evaluateOnlineDriver(
                        dwl.driver,
                        dwl.location,
                        pickupLocation,
                        dropoffLocation,
                        scheduledTime,
                        timeToPickup,
                        rideDuration
                );
            } else {
                availability = evaluateDrivingDriver(
                        dwl.driver,
                        pickupLocation,
                        dropoffLocation,
                        scheduledTime,
                        rideDuration
                );
            }

            if (availability != null && availability.available()) {
                availableDrivers.put(dwl.driver, new DriverAvailabilityInfo(
                        availability.totalTimeToPickup(),
                        dwl.location
                ));
            }
        }

        return availableDrivers.entrySet().stream()
                .min(Comparator.comparingLong(e -> e.getValue().totalTimeToPickup))
                .map(e -> new DriverWithAvailabilityAndLocation(
                        e.getKey(),
                        e.getValue().totalTimeToPickup,
                        e.getValue().location
                ))
                .orElse(null);
    }

    private String getAddressFromCoordinate(Coordinate coordinate) {
        try {
            String url = String.format(Locale.US,
                    "https://api.mapbox.com/geocoding/v5/mapbox.places/%f,%f.json?access_token=%s",
                    coordinate.getLon(),
                    coordinate.getLat(),
                    mapboxApiKey
            );

            RestTemplate restTemplate = new RestTemplate();
            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("features")) {
                List<Map<String, Object>> features = (List<Map<String, Object>>) response.get("features");
                if (!features.isEmpty()) {
                    return (String) features.get(0).get("place_name");
                }
            }
        } catch (Exception e) {
            System.err.println("Reverse geocoding error: " + e.getMessage());
        }

        return String.format("%.6f, %.6f", coordinate.getLat(), coordinate.getLon());
    }

    private record DriverAvailability(boolean available, long totalTimeToPickup) {}

    private record DriverWithLocation(Driver driver, Coordinate location, DriverStatus status) {}

    private record DriverAvailabilityInfo(long totalTimeToPickup, Coordinate location) {}

    private record DriverWithAvailabilityAndLocation(
            Driver driver,
            long estimatedPickupMinutes,
            Coordinate location
    ) {}

    private DriverAvailability evaluateOnlineDriver(
            Driver driver,
            Coordinate driverLocation,
            Coordinate pickupLocation,
            Coordinate dropoffLocation,
            LocalDateTime scheduledTime,
            long timeToPickup,
            long rideDuration) {

        if (!checkUptime(driver, timeToPickup, rideDuration)) {
            return null;
        }

        List<ScheduledRide> scheduledRides = getScheduledRides(driver, LocalDateTime.now());

        if (scheduledRides.isEmpty()) {
            return new DriverAvailability(true, timeToPickup);
        } else {
            return evaluateWithScheduledRides(
                    driverLocation,
                    scheduledRides,
                    pickupLocation,
                    dropoffLocation,
                    scheduledTime,
                    rideDuration
            );
        }
    }

    private DriverAvailability evaluateDrivingDriver(
            Driver driver,
            Coordinate pickupLocation,
            Coordinate dropoffLocation,
            LocalDateTime scheduledTime,
            long rideDuration) {

        Optional<Ride> currentRide = rideRepository.findFirstByDriverAndStatusOrderByStartDesc(
                driver, RideStatus.Ongoing);
        Optional<Ride> pendingRide = rideRepository.findFirstByDriverAndStatusOrderByStartDesc(
                driver, RideStatus.Pending);

        if (currentRide.isEmpty()) {
            if (pendingRide.isEmpty()) return null;
            currentRide = pendingRide;
        }

        LocalDateTime estimatedTimeArrival = currentRide.get().getEstimatedTimeArrival();
        if (estimatedTimeArrival == null) {
            return null;
        }

        List<Coordinate> stations = currentRide.get().getRoute().getStations();
        if (stations == null || stations.isEmpty()) {
            return null;
        }
        Coordinate currentDropoffLocation = stations.get(stations.size() - 1);
        if (currentDropoffLocation == null) {
            return null;
        }

        LocalDateTime rideEndTime = currentRide.get().getFinish() != null
                ? currentRide.get().getFinish()
                : estimatedTimeArrival;

        long minutesUntilCurrentRideEnds = Duration.between(LocalDateTime.now(), rideEndTime).toMinutes();
        if (minutesUntilCurrentRideEnds < 0) {
            return null;
        }

        long timeFromDropoffToPickup = calculateTravelTimeWithCache(currentDropoffLocation, pickupLocation);
        if (timeFromDropoffToPickup < 0) {
            return null;
        }

        long totalTimeToPickup = minutesUntilCurrentRideEnds + timeFromDropoffToPickup;

        if (totalTimeToPickup > MAX_PICKUP_TIME_MINUTES && scheduledTime == null) {
            return null;
        }
        if (!checkUptime(driver, totalTimeToPickup, rideDuration)) {
            return null;
        }

        LocalDateTime estimatedArrivalAtPickup = LocalDateTime.now().plusMinutes(totalTimeToPickup);

        if (scheduledTime != null && estimatedArrivalAtPickup.isAfter(scheduledTime)) {
            return null;
        }

        List<ScheduledRide> scheduledRides = getScheduledRides(driver, estimatedTimeArrival);

        if (!scheduledRides.isEmpty()) {
            return evaluateWithScheduledRides(
                    currentDropoffLocation,
                    scheduledRides,
                    pickupLocation,
                    dropoffLocation,
                    estimatedTimeArrival,
                    rideDuration
            );
        }

        return new DriverAvailability(true, totalTimeToPickup);
    }

    private DriverAvailability evaluateWithScheduledRides(
            Coordinate startLocation,
            List<ScheduledRide> scheduledRides,
            Coordinate pickupLocation,
            Coordinate dropoffLocation,
            LocalDateTime scheduledTime,
            long rideDuration) {

        scheduledRides = new ArrayList<>(scheduledRides);
        scheduledRides.sort(Comparator.comparing(ScheduledRide::getScheduled));

        if (scheduledTime == null) {
            return evaluateImmediateRide(
                    startLocation,
                    scheduledRides,
                    pickupLocation,
                    dropoffLocation,
                    rideDuration
            );
        }

        return evaluateScheduledRide(
                startLocation,
                scheduledRides,
                pickupLocation,
                dropoffLocation,
                scheduledTime,
                rideDuration
        );
    }

    private DriverAvailability evaluateImmediateRide(
            Coordinate startLocation,
            List<ScheduledRide> scheduledRides,
            Coordinate pickupLocation,
            Coordinate dropoffLocation,
            long rideDuration) {

        long timeToPickup = calculateTravelTimeWithCache(startLocation, pickupLocation);
        if (timeToPickup < 0) {
            return null;
        }

        LocalDateTime arrivalTime = LocalDateTime.now().plusMinutes(timeToPickup);

        if (scheduledRides.isEmpty()) {
            return new DriverAvailability(true, timeToPickup);
        }

        ScheduledRide firstScheduled = scheduledRides.get(0);
        LocalDateTime dropoffTime = arrivalTime.plusMinutes(rideDuration);

        List<Coordinate> firstStations = firstScheduled.getRoute().getStations();
        if (firstStations == null || firstStations.isEmpty()) {
            return null;
        }
        Coordinate firstScheduledPickup = firstStations.get(0);
        if (firstScheduledPickup == null) {
            return null;
        }

        long timeToFirstScheduled = calculateTravelTimeWithCache(dropoffLocation, firstScheduledPickup);
        if (timeToFirstScheduled < 0) {
            return null;
        }

        LocalDateTime arrivalAtFirstScheduled = dropoffTime.plusMinutes(timeToFirstScheduled);

        if (arrivalAtFirstScheduled.isAfter(firstScheduled.getScheduled().plusMinutes(5))) {
            return null;
        }

        return new DriverAvailability(true, timeToPickup);
    }

    private DriverAvailability evaluateScheduledRide(
            Coordinate startLocation,
            List<ScheduledRide> scheduledRides,
            Coordinate pickupLocation,
            Coordinate dropoffLocation,
            LocalDateTime scheduledTime,
            long rideDuration) {

        Coordinate currentLocation = startLocation;
        LocalDateTime currentTime = LocalDateTime.now();

        // Try before first scheduled ride
        if (!scheduledRides.isEmpty()) {
            DriverAvailability beforeFirst = tryInsertBeforeFirstScheduled(
                    currentLocation,
                    currentTime,
                    scheduledRides.get(0),
                    pickupLocation,
                    dropoffLocation,
                    scheduledTime,
                    rideDuration
            );
            if (beforeFirst != null) {
                return beforeFirst;
            }
        }

        // Try between scheduled rides
        for (int i = 0; i < scheduledRides.size(); i++) {
            ScheduledRide currentScheduled = scheduledRides.get(i);

            List<Coordinate> currentStations = currentScheduled.getRoute().getStations();
            if (currentStations == null || currentStations.size() < 2) {
                return null;
            }
            Coordinate currentPickup = currentStations.get(0);
            Coordinate currentDropoff = currentStations.get(currentStations.size() - 1);
            if (currentPickup == null || currentDropoff == null) {
                return null;
            }

            if (currentScheduled.getFinish() == null)
                currentTime = currentScheduled.getEstimatedTimeArrival().isAfter(currentTime) ? currentScheduled.getEstimatedTimeArrival() : currentTime;
            else currentTime = currentScheduled.getFinish().isAfter(currentTime) ? currentScheduled.getFinish() : currentTime;
            currentLocation = currentDropoff;

            if (i < scheduledRides.size() - 1) {
                ScheduledRide nextScheduled = scheduledRides.get(i + 1);

                DriverAvailability betweenRides = tryInsertBetweenScheduled(
                        currentLocation,
                        currentTime,
                        nextScheduled,
                        pickupLocation,
                        dropoffLocation,
                        scheduledTime,
                        rideDuration
                );

                if (betweenRides != null) {
                    return betweenRides;
                }
            }
        }

        // Try after all scheduled rides
        long timeToNewPickup = calculateTravelTimeWithCache(currentLocation, pickupLocation);
        if (timeToNewPickup < 0) return null;

        currentTime = currentTime.plusMinutes(timeToNewPickup);

        if (currentTime.isAfter(scheduledTime.plusMinutes(2))) return null;

        long totalTimeToPickup = Duration.between(LocalDateTime.now(), currentTime).toMinutes();
        return new DriverAvailability(true, totalTimeToPickup);
    }

    private DriverAvailability tryInsertBeforeFirstScheduled(
            Coordinate currentLocation,
            LocalDateTime currentTime,
            ScheduledRide firstScheduled,
            Coordinate pickupLocation,
            Coordinate dropoffLocation,
            LocalDateTime scheduledTime,
            long rideDuration) {

        long timeToPickup = calculateTravelTimeWithCache(currentLocation, pickupLocation);
        if (timeToPickup < 0) return null;

        LocalDateTime arrivalAtPickup = currentTime.plusMinutes(timeToPickup);
        LocalDateTime newDropoffTime = arrivalAtPickup.plusMinutes(rideDuration);

        LocalDateTime firstRideStart = firstScheduled.getScheduled();
        if (newDropoffTime.isAfter(firstRideStart.minusMinutes(2))) {
            return null;
        }

        long totalTimeToPickup = Duration.between(LocalDateTime.now(), arrivalAtPickup).toMinutes();
        return new DriverAvailability(true, totalTimeToPickup);
    }

    private DriverAvailability tryInsertBetweenScheduled(
            Coordinate currentLocation,
            LocalDateTime currentTime,
            ScheduledRide nextScheduled,
            Coordinate pickupLocation,
            Coordinate dropoffLocation,
            LocalDateTime scheduledTime,
            long rideDuration) {

        long timeToPickup = calculateTravelTimeWithCache(currentLocation, pickupLocation);
        if (timeToPickup < 0) return null;

        LocalDateTime arrivalAtPickup = currentTime.plusMinutes(timeToPickup);
        LocalDateTime newDropoffTime = arrivalAtPickup.plusMinutes(rideDuration);

        LocalDateTime nextRideStart = nextScheduled.getScheduled();
        if (newDropoffTime.isAfter(nextRideStart.minusMinutes(2))) {
            return null;
        }

        long totalTimeToPickup = Duration.between(LocalDateTime.now(), arrivalAtPickup).toMinutes();
        return new DriverAvailability(true, totalTimeToPickup);
    }


    private boolean checkUptime(Driver driver, long timeToPickup, long rideDuration) {
        int currentUptime = driver.getUptime();

        if (currentUptime >= MAX_UPTIME_MINUTES) {
            return false;
        }

        long totalAdditionalTime = timeToPickup + rideDuration;
        long projectedUptime = currentUptime + totalAdditionalTime;

        return projectedUptime <= (MAX_UPTIME_MINUTES + UPTIME_TOLERANCE_MINUTES);
    }

    private List<ScheduledRide> getScheduledRides(Driver driver, LocalDateTime fromTime) {
        LocalDateTime endTime = fromTime.plusHours(MAX_SCHEDULE_HOURS);

        return scheduledRideRepository.findByDriverAndScheduledBetweenOrderByScheduledAsc(
                        driver,
                        fromTime.minusMinutes(30),
                        endTime
                ).stream()
                .filter(sr ->
                        sr.getFinish() == null
                                ? sr.getEstimatedTimeArrival().isAfter(LocalDateTime.now())
                                : sr.getFinish().isAfter(LocalDateTime.now())
                ).toList();

    }

    private List<Driver> findByStatus(DriverStatus status, String vehicleType, Boolean needsBaby, Boolean needsPet) {
        return driverRepository.findByStatus(status).stream()
                .filter(d -> {
                    if (d.getVehicle() == null || d.getVehicle().getVehicleType() == null) {
                        return false;
                    }

                    if (!d.getVehicle().getVehicleType().getName().equals(vehicleType)) {
                        return false;
                    }

                    if (needsBaby != null && needsBaby && !d.getVehicle().getBabySeat()) {
                        return false;
                    }

                    if (needsPet != null && needsPet && !d.getVehicle().getPetFriendly()) {
                        return false;
                    }

                    return true;
                })
                .toList();
    }

    private Coordinate getDriverLocation(Driver driver) {
        if (driver.getStatus() == DriverStatus.DRIVING) {
            Optional<Ride> currentRide = rideRepository.findFirstByDriverAndStatusOrderByStartDesc(
                    driver, RideStatus.Ongoing);
            if (currentRide.isPresent()) {
                List<Coordinate> stations = currentRide.get().getRoute().getStations();
                if (!stations.isEmpty()) {
                    return stations.get(stations.size() - 1);
                }
            } else {
                currentRide = rideRepository.findFirstByDriverAndStatusOrderByStartDesc(
                        driver, RideStatus.Pending);
                if (currentRide.isPresent()) {
                    List<Coordinate> stations = currentRide.get().getRoute().getStations();
                    if (!stations.isEmpty()) {
                        return stations.get(0);
                    }
                }
            }
        }

        Coordinate location = driver.getVehicle() != null ? driver.getVehicle().getLocation() : null;
        if (location == null) {
            location = geocodeAddressWithCache(driver.getHomeAddress());
        }
        return location;
    }

    public Coordinate geocodeAddressWithCache(String address) {
        Optional<Coordinate> cached = coordinateRepository
                .findByAddress(address);

        if (cached.isPresent()) {
            return cached.get();
        }

        Coordinate coord = callMapboxGeocoding(address);

        if (coord != null) {
            Coordinate newCache = new Coordinate();
            newCache.setAddress(address);
            newCache.setLat(coord.getLat());
            newCache.setLon(coord.getLon());
            coord = saveOrGetCoordinate(newCache);
        }

        return coord;
    }

    private Coordinate callMapboxGeocoding(String address) {
        try {
            String fullAddress = address;
            if (!address.toLowerCase().contains("novi sad")) {
                fullAddress += ", Novi Sad, Serbia";
            }

            String url = String.format(
                    "https://api.mapbox.com/geocoding/v5/mapbox.places/%s.json?access_token=%s&country=RS&limit=1",
                    fullAddress,
                    mapboxApiKey
            );

            RestTemplate restTemplate = new RestTemplate();
            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("features")) {
                List<Map<String, Object>> features = (List<Map<String, Object>>) response.get("features");
                if (!features.isEmpty()) {
                    List<Double> coordinates = (List<Double>) features.get(0).get("center");
                    Coordinate newCoord = new Coordinate(
                            coordinates.get(1),
                            coordinates.get(0),
                            fullAddress
                    );

                    return saveOrGetCoordinate(newCoord);
                }
            }
        } catch (Exception e) {
            System.err.println("Geocoding error: " + e.getMessage());
        }
        return null;
    }

    private long calculateTravelTimeWithCache(Coordinate from, Coordinate to) {
        String cacheKey = formatCoordinateKey(from, to);

        CachedTravelTime cached = travelTimeCache.get(cacheKey);
        if (cached != null && !cached.isExpired()) {
            return cached.travelTime;
        }

        long travelTime = callMapboxDirections(from, to);

        if (travelTime >= 0) {
            travelTimeCache.put(cacheKey, new CachedTravelTime(travelTime));
        }

        return travelTime;
    }

    private long callMapboxDirections(Coordinate from, Coordinate to) {
        try {
            Coordinate fromCoord = from;
            Coordinate toCoord = to;

            if (from.getLat() == null || from.getLon() == null) {
                if (from.getAddress() != null && !from.getAddress().trim().isEmpty()) {
                    Coordinate geocoded = geocodeAddressWithCache(from.getAddress());
                    if (geocoded == null) {
                        System.err.println("Failed to geocode 'from' address: " + from.getAddress());
                        return -1;
                    }
                    from.setLat(geocoded.getLat());
                    from.setLon(geocoded.getLon());
                    fromCoord = saveOrGetCoordinate(from);
                } else {
                    System.err.println("'From' coordinate has no lat/lon and no address to geocode");
                    return -1;
                }
            }

            if (to.getLat() == null || to.getLon() == null) {
                if (to.getAddress() != null && !to.getAddress().trim().isEmpty()) {
                    Coordinate geocoded = geocodeAddressWithCache(to.getAddress());
                    if (geocoded == null) {
                        System.err.println("Failed to geocode 'to' address: " + to.getAddress());
                        return -1;
                    }
                    to.setLat(geocoded.getLat());
                    to.setLon(geocoded.getLon());
                    toCoord = saveOrGetCoordinate(to);
                } else {
                    System.err.println("'To' coordinate has no lat/lon and no address to geocode");
                    return -1;
                }
            }

            String url = String.format(Locale.US,
                    "https://api.mapbox.com/directions/v5/mapbox/driving/%f,%f;%f,%f?access_token=%s",
                    fromCoord.getLon(), fromCoord.getLat(),
                    toCoord.getLon(), toCoord.getLat(),
                    mapboxApiKey
            );

            RestTemplate restTemplate = new RestTemplate();
            Map response = restTemplate.getForObject(url, Map.class);

            if (response != null && response.containsKey("routes")) {
                List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
                if (!routes.isEmpty()) {
                    Number durationNum = (Number) routes.get(0).get("duration");
                    double duration = durationNum.doubleValue();
                    return Math.round(duration / 60.0);
                }
            }
        } catch (Exception e) {
            System.err.println("Mapbox API error: " + e.getMessage());
        }
        return -1;
    }

    private Map<String, Long> calculateMultipleTravelTimes(
            Coordinate source,
            List<Coordinate> destinations) {

        if (destinations.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Long> results = new HashMap<>();
        List<Coordinate> uncachedDestinations = new ArrayList<>();

        for (Coordinate dest : destinations) {
            String key = formatCoordinateKey(source, dest);
            CachedTravelTime cached = travelTimeCache.get(key);

            if (cached != null && !cached.isExpired()) {
                results.put(key, cached.travelTime);
            } else {
                uncachedDestinations.add(dest);
            }
        }

        if (uncachedDestinations.isEmpty()) {
            return results;
        }

        List<List<Coordinate>> batches = new ArrayList<>();
        for (int i = 0; i < uncachedDestinations.size(); i += 24) {
            batches.add(uncachedDestinations.subList(
                    i, Math.min(i + 24, uncachedDestinations.size())
            ));
        }

        for (List<Coordinate> batch : batches) {
            Map<String, Long> batchResults = callMapboxMatrix(source, batch);
            results.putAll(batchResults);

            for (Map.Entry<String, Long> entry : batchResults.entrySet()) {
                travelTimeCache.put(entry.getKey(), new CachedTravelTime(entry.getValue()));
            }
        }

        return results;
    }

    private Map<String, Long> callMapboxMatrix(Coordinate source, List<Coordinate> destinations) {
        try {
            StringBuilder coords = new StringBuilder()
                    .append(String.format(Locale.US, "%.6f,%.6f", source.getLon(), source.getLat()));

            for (Coordinate dest : destinations) {
                coords.append(";")
                        .append(String.format(Locale.US, "%.6f,%.6f", dest.getLon(), dest.getLat()));
            }


            String url = String.format(Locale.US,
                    "https://api.mapbox.com/directions-matrix/v1/mapbox/driving/%s?sources=0&access_token=%s",
                    coords.toString(),
                    mapboxApiKey
            );

            RestTemplate restTemplate = new RestTemplate();
            Map response = restTemplate.getForObject(url, Map.class);

            Map<String, Long> results = new HashMap<>();

            if (response != null && response.containsKey("durations")) {
                List<List<Number>> durations = (List<List<Number>>) response.get("durations");
                if (!durations.isEmpty()) {
                    List<Number> sourceDurations = durations.get(0);
                    for (int i = 0; i < destinations.size(); i++) {
                        Number durationNum = sourceDurations.get(i + 1);
                        if (durationNum != null) {
                            double duration = durationNum.doubleValue();
                            String key = formatCoordinateKey(source, destinations.get(i));
                            results.put(key, Math.round(duration / 60.0));
                        }
                    }
                }
            }

            return results;
        } catch (Exception e) {
            System.err.println("Matrix API error: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    private double calculateHaversineDistance(Coordinate from, Coordinate to) {
        final double R = 6371;

        double lat1Rad = Math.toRadians(from.getLat());
        double lat2Rad = Math.toRadians(to.getLat());
        double dLat = Math.toRadians(to.getLat() - from.getLat());
        double dLon = Math.toRadians(to.getLon() - from.getLon());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private String formatCoordinateKey(Coordinate from, Coordinate to) {
        return String.format("%.6f,%.6f->%.6f,%.6f",
                from.getLat(), from.getLon(), to.getLat(), to.getLon());
    }

    public void updateAllDriverLocation() {
        for (Driver driver : driverRepository.findAll()) {
            if (driver.getVehicle().getLocation() == null) {
                Coordinate coord = geocodeAddressWithCache(driver.getHomeAddress());
                if (coord != null) {
                    driver.getVehicle().setLocation(coord);
                    vehicleRepository.save(driver.getVehicle());
                }
            }
        }
    }

    public void updateDriverLocation(Long driverId, String address) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Coordinate newLocation = geocodeAddressWithCache(address);
        if (newLocation == null) {
            throw new RuntimeException("Failed to geocode address");
        }

        Vehicle vehicle = driver.getVehicle();
        vehicle.setLocation(newLocation);
        vehicleRepository.save(vehicle);
    }

    private static class CachedTravelTime {
        final long travelTime;
        final long timestamp;

        CachedTravelTime(long travelTime) {
            this.travelTime = travelTime;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > TRAVEL_TIME_CACHE_TTL_MS;
        }
    }

    @Scheduled(fixedRate = 600000)
    public void cleanExpiredCache() {
        travelTimeCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}