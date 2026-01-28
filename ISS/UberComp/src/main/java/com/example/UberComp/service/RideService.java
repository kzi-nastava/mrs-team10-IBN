package com.example.UberComp.service;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.dto.ride.*;
import com.example.UberComp.dto.vehicle.VehicleLocationDTO;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import com.example.UberComp.utils.EmailUtils;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class RideService {

    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private ScheduledRideRepository scheduledRideRepository;
    @Autowired
    private CoordinateRepository coordinateRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PanicSignalRepository panicSignalRepository;
    @Autowired
    private FavoriteRouteRepository favoriteRouteRepository;
    @Autowired
    private EmailUtils emailUtils;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional
    public IncomingRideDTO getIncomingRide(Driver driver){
        Optional<Ride> rideOptional = rideRepository.findFirstByDriverAndStatusOrderByStartAsc(driver, RideStatus.Pending);
        if(rideOptional.isEmpty()) return null;
        IncomingRideDTO newRide = new IncomingRideDTO(rideOptional.get());
        return newRide;
    }

    public Page<GetRideDTO> getRidesDriver(Long driverId, String sortParam,
                                           LocalDateTime startFrom, LocalDateTime startTo,
                                           int page, int size){
        Sort sort = buildSort(sortParam);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Ride> rides;
        if (startFrom != null && startTo != null) {
            rides = rideRepository.getRidesDriverWithDateFilter(driverId, startFrom, startTo, pageable);
        } else if (startFrom != null) {
            rides = rideRepository.getRidesDriverFromDate(driverId, startFrom, pageable);
        } else if (startTo != null) {
            rides = rideRepository.getRidesDriverToDate(driverId, startTo, pageable);
        } else {
            rides = rideRepository.getRidesDriver(driverId, pageable);
        }

        return new PageImpl<>(rides.getContent().stream().map(GetRideDTO::new).toList(), pageable, rides.getTotalElements());
    }

    public Page<GetRideDTO> getRidesAdmin(String sortParam,
                                           LocalDateTime startFrom, LocalDateTime startTo,
                                           int page, int size){
        Sort sort = buildSort(sortParam);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Ride> rides;
        if (startFrom != null && startTo != null) {
            rides = rideRepository.getRidesAdminWithDateFilter(startFrom, startTo, pageable);
        } else if (startFrom != null) {
            rides = rideRepository.getRidesAdminFromDate(startFrom, pageable);
        } else if (startTo != null) {
            rides = rideRepository.getRidesAdminToDate(startTo, pageable);
        } else {
            rides = rideRepository.getRidesAdmin(pageable);
        }

        return new PageImpl<>(rides.getContent().stream().map(GetRideDTO::new).toList(), pageable, rides.getTotalElements());
    }

    public Page<GetRideDTO> getRidesPassenger(Long userId, String sortParam,
                                              LocalDateTime startFrom, LocalDateTime startTo,
                                              int page, int size) {
        Sort sort = buildSort(sortParam);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Ride> rides;
        if (startFrom != null && startTo != null) {
            rides = rideRepository.getRidesPassengerWithDateFilter(userId, startFrom, startTo, pageable);
        } else if (startFrom != null) {
            rides = rideRepository.getRidesPassengerFromDate(userId, startFrom, pageable);
        } else if (startTo != null) {
            rides = rideRepository.getRidesPassengerToDate(userId, startTo, pageable);
        } else {
            rides = rideRepository.getRidesPassenger(userId, pageable);
        }

        return new PageImpl<>(rides.getContent().stream().map(GetRideDTO::new).toList(), pageable, rides.getTotalElements());
    }

    private Sort buildSort(String sortParam) {
        if(sortParam == null) return Sort.by(Sort.Direction.DESC, "start");
        return switch (sortParam) {
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "start-asc" -> Sort.by(Sort.Direction.ASC, "start");
            case "start-desc" -> Sort.by(Sort.Direction.DESC, "start");
            case "end-asc" -> Sort.by(Sort.Direction.ASC, "estimatedTimeArrival");
            case "end-desc" -> Sort.by(Sort.Direction.DESC, "estimatedTimeArrival");
            default -> Sort.by(Sort.Direction.DESC, "start"); // default sorting
        };
    }

    @Transactional(readOnly = true)
    public GetRideDetailsDTO getRide(Long rideId){
        Ride ride = rideRepository.getRideWithRoute(rideId);
        ride = rideRepository.getRideWithPassengers(rideId);
        return new GetRideDetailsDTO(ride);
    }

    @Transactional(readOnly = true)
    public ArrayList<GetVehiclePositionDTO> getActiveRides() {
        ArrayList<GetVehiclePositionDTO> activeRides = new ArrayList<>();
        List<Driver> activeDrivers = driverRepository.findByStatus(DriverStatus.DRIVING);
        for (Driver driver : activeDrivers) {
            Ride ride = rideRepository.findFirstByDriver_IdOrderByStartDesc(driver.getId());
            if (ride != null)
                activeRides.add(new GetVehiclePositionDTO(ride, true));
        }
        activeDrivers = driverRepository.findByStatus(DriverStatus.ONLINE);
        for (Driver driver : activeDrivers) {
            GetVehiclePositionDTO vehiclePositionDTO = new GetVehiclePositionDTO();
            vehiclePositionDTO.setVehicleLocation(new VehicleLocationDTO(driver.getVehicle().getLocation()));
            vehiclePositionDTO.setBusy(false);
            activeRides.add(vehiclePositionDTO);
        }
        return activeRides;
    }

    @Transactional(readOnly = true)
    public GetVehiclePositionDTO getTrackingRide(Long id) {
        Optional<Ride> ride = rideRepository.findFirstByPassengersIdAndStatusOrderByStartAsc(id, RideStatus.Ongoing);
        if(ride.isPresent()) {
            if (ride.get().getEstimatedTimeArrival().isBefore(LocalDateTime.now()))
                return new GetVehiclePositionDTO();
            return new GetVehiclePositionDTO(ride.get(), true);
        }
        return new GetVehiclePositionDTO();
    }

    public Page<GetRideDTO> getScheduledRidesForDriver(Long id, Pageable pageable){
        Page<ScheduledRide> scheduledRides = scheduledRideRepository.getScheduledRidesForDriver(id, pageable);
        return new PageImpl<>(scheduledRides.stream().map(GetRideDTO::new).toList(), pageable, scheduledRides.getTotalElements());

    }

    public UpdatedStatusRideDTO updateRideStatus(UpdateStatusRideDTO updateRideDTO){ return new UpdatedStatusRideDTO();}

    public FinishedRideDTO endRide(Long rideId, RideMomentDTO finish){
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        ride.setStatus(RideStatus.Finished);
        Driver driver = ride.getDriver();
        driver.setStatus(DriverStatus.ONLINE);
        Instant instant = Instant.parse(finish.getIsotime());
        ride.setFinish(instant.atZone(ZoneId.of("UTC")).toLocalDateTime());        // ride.setPrice(); price calculation
        rideRepository.save(ride);
        Vehicle vehicle = driver.getVehicle();
        vehicle.setLocation(ride.getRoute().getStations().get(ride.getRoute().getStations().size()-1));
        vehicleRepository.save(vehicle);
        driverRepository.save(driver);
        emailUtils.sendEmailWhenRideIsFinished("ignjaticivana70@gmail.com", rideId);
        return new FinishedRideDTO(ride);
    }

    public FinishedRideDTO stopRide(StopRideDTO stopRideDTO, boolean panic) {
        Ride ride = rideRepository.findById(stopRideDTO.getId()).orElseThrow();
        Route stoppedRoute = ride.getRoute();
        Coordinate newCoordinate = new Coordinate();
        newCoordinate.setLat(stopRideDTO.getLat());
        newCoordinate.setLon(stopRideDTO.getLon());
        newCoordinate.setAddress(stopRideDTO.getAddress());
        Coordinate savedCoord = saveOrGetCoordinate(newCoordinate);

        List<Coordinate> newStations = ride.getRoute().getStations().subList(0, stopRideDTO.getPassed());
        newStations.add(savedCoord);
        stoppedRoute.setStations(newStations);
        routeRepository.save(stoppedRoute);
        Instant instant = Instant.parse(stopRideDTO.getFinishTime());
        ride.setFinish(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        Driver driver = ride.getDriver();
        if(panic) {
            PanicSignal panicSignal = new PanicSignal();
            panicSignal.setRide(ride);
            panicSignalRepository.save(panicSignal);
            ride.setStatus(RideStatus.Panic);
            ride.setPrice(0.0);
            driver.setStatus(DriverStatus.PANIC);
        }
        else {
            double newPrice = calculatePrice(stopRideDTO.getId(), stopRideDTO.getDistance()).getPrice();
            ride.setPrice(newPrice);
            ride.setStatus(RideStatus.Finished);
            driver.setStatus(DriverStatus.ONLINE);
            emailUtils.sendEmailWhenRideIsFinished("ignjaticivana70@gmail.com", stopRideDTO.getId());
        }
        Vehicle vehicle = driver.getVehicle();
        vehicle.setLocation(ride.getRoute().getStations().get(ride.getRoute().getStations().size()-1));
        vehicleRepository.save(vehicle);
        rideRepository.save(ride);
        driverRepository.save(driver);
        return new FinishedRideDTO(ride);
    }

    public StartedRideDTO startRide(Long id, RideMomentDTO start) {
        Ride started = rideRepository.findById(id).orElseThrow();
        started.setStatus(RideStatus.Ongoing);
        Instant instant = Instant.parse(start.getIsotime());
        started.setStart(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        rideRepository.save(started);
        return new StartedRideDTO(started.getId(), started.getStart());
    }

    public PriceDTO calculatePrice(Long rideId, double distance){
        Ride ride = rideRepository.findById(rideId).get();
        double basePrice = ride.getDriver().getVehicle().getVehicleType().getPrice();
        double totalPrice = basePrice + distance * 120;
        return new PriceDTO(totalPrice);
    }

    public PriceDTO calculatePrice(CreateRideDTO dto) {
        double basePrice = vehicleTypeRepository.findVehicleTypeByName(dto.getVehicleType()).getPrice();
        double totalPrice = basePrice + dto.getDistance() * 120;
        return new PriceDTO(totalPrice);
    }

    public RideOrderResponseDTO buildRideOrderResponse(Long rideId, AvailableDriverDTO availableDriver) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        DriverDTO driverDTO = availableDriver.getDriver();
        Driver driver = driverRepository.findById(driverDTO.getCreateUserDTO().getId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        RideOrderResponseDTO response = new RideOrderResponseDTO();

        response.setRideId(ride.getId());
        response.setPrice(ride.getPrice());
        response.setStatus(ride.getStatus().toString());

        response.setDriverName(driver.getName() + " " + driver.getLastName());
        response.setDriverPhone(driver.getPhone());

        Vehicle vehicle = driver.getVehicle();
        if (vehicle != null) {
            response.setVehicleModel(vehicle.getModel());
            response.setVehiclePlate(vehicle.getPlate());
        }

        response.setVehicleLocation(availableDriver.getVehicleLocation());

        Optional<ScheduledRide> sRide = scheduledRideRepository.findById(ride.getId());
        if (sRide.isPresent()) {
            if (sRide.get().getScheduled() != null) {
                response.setEstimatedPickupMinutes(-1L);
                response.setEstimatedPickupTime(sRide.get().getScheduled().format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        } else {
            LocalDateTime estimatedArrival = LocalDateTime.now()
                    .plusMinutes(availableDriver.getEstimatedPickupMinutes());
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            response.setEstimatedPickupTime(estimatedArrival.format(timeFormatter));
            if (availableDriver.getEstimatedPickupMinutes() == 0)
                availableDriver.setEstimatedPickupMinutes(1L);
            response.setEstimatedPickupMinutes(availableDriver.getEstimatedPickupMinutes());
        }

        return response;
    }

    @Transactional
    public Ride createRide(CreateRideDTO dto, Long passengerId, Long driverId, Long estimatedPickupMinutes) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverId));

        User passenger = userRepository.findById(passengerId)
                .orElseThrow(() -> new RuntimeException("Passenger not found with id: " + passengerId));

        Route route = new Route();
        List<Coordinate> stations = new ArrayList<>();

        Coordinate startCoord = saveOrGetCoordinate(new Coordinate(dto.getStartAddress()));
        stations.add(startCoord);

        if (dto.getStops() != null && !dto.getStops().isEmpty()) {
            for (GetCoordinateDTO stopAddress : dto.getStops()) {
                if (stopAddress != null) {
                    Coordinate stopCoord = saveOrGetCoordinate(new Coordinate(stopAddress));
                    stations.add(stopCoord);
                }
            }
        }

        Coordinate destCoord = saveOrGetCoordinate(new Coordinate(dto.getDestinationAddress()));
        stations.add(destCoord);

        route.setStations(stations);
        route = routeRepository.save(route);

        Set<User> passengers = new HashSet<>();
        passengers.add(passenger);

        if (dto.getPassengerEmails() != null && !dto.getPassengerEmails().isEmpty()) {
            for (String email : dto.getPassengerEmails()) {
                if (email != null && !email.trim().isEmpty()) {
                    // send notification ??
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();
        Ride ride;

        if (dto.getScheduled() != null) {
            ScheduledRide scheduledRide = new ScheduledRide();
            scheduledRide.setScheduled(dto.getScheduled());
            scheduledRide.setRoute(route);
            scheduledRide.setDriver(driver);
            scheduledRide.setPassengers(passengers);
            scheduledRide.setBabies(dto.getBabySeat() != null ? dto.getBabySeat() : false);
            scheduledRide.setPets(dto.getPetFriendly() != null ? dto.getPetFriendly() : false);
            scheduledRide.setPrice(dto.getPrice());
            scheduledRide.setStart(dto.getScheduled());
            scheduledRide.setStatus(RideStatus.Pending);
            scheduledRide.setEstimatedTimeArrival(dto.getScheduled().plusMinutes(dto.getEstimatedDuration()));
            scheduledRide.setFinish(null);
            scheduledRide.setCancellationReason(null);

            ride = rideRepository.save(scheduledRide);

        } else {
            ride = new Ride();
            ride.setRoute(route);
            ride.setDriver(driver);
            ride.setPassengers(passengers);
            ride.setBabies(dto.getBabySeat() != null ? dto.getBabySeat() : false);
            ride.setPets(dto.getPetFriendly() != null ? dto.getPetFriendly() : false);
            ride.setPrice(dto.getPrice());
            ride.setStart(now);
            ride.setStatus(RideStatus.Pending);

            if (estimatedPickupMinutes == 0)
                estimatedPickupMinutes = 1L;
            long totalMinutes = estimatedPickupMinutes + dto.getEstimatedDuration();
            ride.setEstimatedTimeArrival(now.plusMinutes(totalMinutes));

            ride.setFinish(null);
            ride.setCancellationReason(null);

            ride = rideRepository.save(ride);

            driver.setStatus(DriverStatus.DRIVING);
            driverRepository.save(driver);
        }

        return ride;
    }

    @Transactional(readOnly = true)
    public List<FavoriteRouteDTO> getFavoriteRoutes(Account account) {
        List<FavoriteRoute> favs = favoriteRouteRepository.findFavoriteRouteByAccount(account);

        return favs.stream()
                .map(fav -> {
                    fav.getRoute().getStations().size();
                    return new FavoriteRouteDTO(
                            fav.getId(),
                            null,
                            new GetRouteDTO(fav.getRoute())
                    );
                })
                .toList();
    }

    @Transactional
    public FavoriteRouteDTO addRouteToFavorites(Long routeId, Account account) {
        Optional<Route> route = routeRepository.findById(routeId);
        if (!route.isPresent())
            return null;

        Optional<FavoriteRoute> existing = favoriteRouteRepository
                .findByAccountAndRoute(account, route.get());

        if (existing.isPresent()) {
            FavoriteRoute fav = existing.get();
            fav.getRoute().getStations().size();
            return new FavoriteRouteDTO(fav.getId(), null, new GetRouteDTO(fav.getRoute()));
        }

        FavoriteRoute favoriteRoute = new FavoriteRoute();
        favoriteRoute.setAccount(account);
        favoriteRoute.setRoute(route.get());

        FavoriteRoute saved = favoriteRouteRepository.save(favoriteRoute);
        saved.getRoute().getStations().size();

        return new FavoriteRouteDTO(saved.getId(), null, new GetRouteDTO(saved.getRoute()));
    }

    public void removeRouteFromFavorites(Long favoriteRouteId, Account account) {
        FavoriteRoute favoriteRoute = favoriteRouteRepository.findById(favoriteRouteId)
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        if (!favoriteRoute.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        favoriteRouteRepository.delete(favoriteRoute);
    }

    public void removeRouteFromFavoritesByRoute(Long routeId, Account account) {
        Route route = routeRepository.findById(routeId).orElseThrow(() -> new RuntimeException("Route not found"));
        FavoriteRoute favoriteRoute = favoriteRouteRepository.findByRoute(route)
                .orElseThrow(() -> new RuntimeException("Favorite route not found"));

        if (!favoriteRoute.getAccount().getId().equals(account.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        favoriteRouteRepository.delete(favoriteRoute);
    }

    public boolean hasOngoingRide(Long userId) {
        return rideRepository.existsByPassengerIdAndStatus(userId, RideStatus.Ongoing);
    }

    public boolean cancelRide(CancelRideDTO cancelled){
        Optional<Ride> rideOptional = rideRepository.findById(cancelled.getId());
        if(rideOptional.isEmpty()) return false;
        Ride cancelledRide = rideOptional.get();
        if(!cancelled.isCancelledByDriver() &&
                Duration.between(LocalDateTime.now(), cancelledRide.getStart()).toMinutes() < 10)
            return false;
        Driver driver = cancelledRide.getDriver();
        driver.setStatus(DriverStatus.ONLINE);
        cancelledRide.setPrice(0.0);
        if(cancelled.isCancelledByDriver()){
            cancelledRide.setStatus(RideStatus.CancelledByDriver);
        } else {
            cancelledRide.setStatus(RideStatus.CancelledByPassenger);
        }
        cancelledRide.setCancellationReason(cancelled.getCancellationReason());
        driverRepository.save(driver);
        rideRepository.save(cancelledRide);
        return true;
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
}
