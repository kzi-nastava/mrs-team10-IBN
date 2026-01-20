package com.example.UberComp.service;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.dto.ride.*;
import com.example.UberComp.dto.vehicle.VehicleLocationDTO;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
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

    public IncomingRideDTO getIncomingRide(){
        IncomingRideDTO newRide = new IncomingRideDTO();
        newRide.setId(0L);
        Route route = new Route();
        route.setId(1L);
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(1L, 45.2633078, 19.8311535, "Bulevar oslobođenja 7"));
        coordinates.add(new Coordinate(2L, 45.240657, 19.812193, "Bulevar Patrijarha Pavla 60"));
        coordinates.add(new Coordinate(3L, 45.2626426, 19.8150372, "Kornelija Stankovića 15"));
        route.setStations(coordinates);
        newRide.setRoute(route);
        return newRide;
    }

    public Collection<GetRideDTO> getRidesDriver(Long driverId){
        return rideRepository.getRidesDriver(driverId)
                .stream()
                .map(GetRideDTO::new)
                .toList();
    }

    public Collection<GetRideDTO> getRidesPassenger(Long passengerId){
        return rideRepository.getRidesPassenger(passengerId)
                .stream()
                .map(GetRideDTO::new)
                .toList();
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
        for(Driver driver: activeDrivers) {
            Ride ride = rideRepository.findFirstByDriver_IdOrderByStartDesc(driver.getId());
            if (ride != null)
                activeRides.add(new GetVehiclePositionDTO(ride, true));
        }
        activeDrivers = driverRepository.findByStatus(DriverStatus.ONLINE);
        for(Driver driver: activeDrivers) {
            Ride ride = rideRepository.findFirstByDriver_IdOrderByStartDesc(driver.getId());
            if (ride != null)
                activeRides.add(new GetVehiclePositionDTO(ride, false));
        }
        return activeRides;
    }

    @Transactional(readOnly = true)
    public GetVehiclePositionDTO getTrackingRide(Long id) {
        Ride ride = rideRepository.findFirstByPassengersIdOrderByStartDesc(id);
        if(ride != null) {
            if (ride.getEstimatedTimeArrival().isBefore(LocalDateTime.now()))
                return new GetVehiclePositionDTO();
            return new GetVehiclePositionDTO(ride, true);
        }
        return new GetVehiclePositionDTO();
    }

    public Collection<GetRideDTO> getScheduledRidesForDriver(Long id){
        List<ScheduledRide> scheduledRides = scheduledRideRepository.getScheduledRidesForDriver(id);
        ArrayList<ScheduledRide> futureScheduledRides = new ArrayList<>();
        for (ScheduledRide ride: scheduledRides){
            if (ride.getStart().isAfter(LocalDateTime.now()))
                futureScheduledRides.add(ride);
        }
        return futureScheduledRides
                .stream()
                .map(GetRideDTO::new)
                .toList();
    }

    public UpdatedStatusRideDTO updateRideStatus(UpdateStatusRideDTO updateRideDTO){ return new UpdatedStatusRideDTO();}

    public FinishedRideDTO endRide(Long rideId, RideMomentDTO finish){
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        ride.setStatus(RideStatus.Finished);
        ride.setFinish(LocalDateTime.parse(finish.getIsotime()));
        // ride.setPrice(); price calculation
        rideRepository.save(ride);
        return new FinishedRideDTO(ride);
    }

    public FinishedRideDTO stopRide(StopRideDTO stopRideDTO, boolean panic) {
        Ride ride = rideRepository.findById(stopRideDTO.getId()).orElseThrow();
        Coordinate newCoordinate = new Coordinate();
        newCoordinate.setLat(stopRideDTO.getLat());
        newCoordinate.setLon(stopRideDTO.getLon());
        newCoordinate.setAddress(stopRideDTO.getAddress());
        Coordinate savedCoord = coordinateRepository.save(newCoordinate);
        List<Coordinate> newStations = ride.getRoute().getStations().subList(0, stopRideDTO.getPassed());
        newStations.add(savedCoord);
        Route newRoute = new Route();
        newRoute.setStations(newStations);
        Route savedRoute = routeRepository.save(newRoute);
        ride.setRoute(savedRoute);
        ride.setFinish(LocalDateTime.parse(stopRideDTO.getFinishTime()));
        Driver driver = ride.getDriver();
        if(panic) {
            PanicSignal panicSignal = new PanicSignal();
            panicSignal.setRide(ride);
            panicSignalRepository.save(panicSignal);
            ride.setStatus(RideStatus.Panic);
            driver.setStatus(DriverStatus.PANIC);
        }
        else {
            ride.setStatus(RideStatus.Finished);
            driver.setStatus(DriverStatus.ONLINE);
        }
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

        Coordinate startCoord = new Coordinate();
        startCoord.setAddress(dto.getStartAddress());
        startCoord = coordinateRepository.save(startCoord);
        stations.add(startCoord);

        if (dto.getStops() != null && !dto.getStops().isEmpty()) {
            for (String stopAddress : dto.getStops()) {
                if (stopAddress != null && !stopAddress.trim().isEmpty()) {
                    Coordinate stopCoord = new Coordinate();
                    stopCoord.setAddress(stopAddress);
                    stopCoord = coordinateRepository.save(stopCoord);
                    stations.add(stopCoord);
                }
            }
        }

        Coordinate destCoord = new Coordinate();
        destCoord.setAddress(dto.getDestinationAddress());
        destCoord = coordinateRepository.save(destCoord);
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
}
