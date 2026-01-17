package com.example.UberComp.service;

import com.example.UberComp.dto.driver.AvailableDriverDTO;
import com.example.UberComp.dto.driver.DriverDTO;
import com.example.UberComp.dto.driver.GetVehiclePositionDTO;
import com.example.UberComp.dto.ride.*;
import com.example.UberComp.dto.vehicle.VehicleLocationDTO;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    public UpdatedStatusRideDTO updateRideStatus(UpdateStatusRideDTO updateRideDTO){ return new UpdatedStatusRideDTO();}
    public GetTrackingRideDTO getTrackingRide(Long rideId){ return new GetTrackingRideDTO();}

    public FinishedRideDTO endRide(Long rideId, RideMomentDTO finish){
        Ride ride = rideRepository.findById(rideId).orElseThrow();
        ride.setStatus(RideStatus.Finished);
        ride.setFinish(LocalDateTime.parse(finish.getIsotime()));
        // ride.setPrice(); price calculation
        rideRepository.save(ride);
        return new FinishedRideDTO(ride);
    }

    public FinishedRideDTO stopRide(Long rideId, StopRideDTO stopRideDTO) {
        Ride ride = rideRepository.findById(rideId).orElseThrow();
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
        rideRepository.save(ride);
        return new FinishedRideDTO(ride);
    }

    public StartedRideDTO startRide(Long id, RideMomentDTO start) {
        Ride started = rideRepository.findById(id).orElseThrow();
        started.setStatus(RideStatus.Ongoing);
        started.setStart(LocalDateTime.parse(start.getIsotime()));
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
            response.setEstimatedPickupMinutes(-1L);
            response.setEstimatedPickupTime(sRide.get().getScheduled().format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            LocalDateTime estimatedArrival = LocalDateTime.now()
                    .plusMinutes(availableDriver.getEstimatedPickupMinutes());
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            response.setEstimatedPickupTime(estimatedArrival.format(timeFormatter));
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
            scheduledRide.setPanic(false);
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
            ride.setPanic(false);
            ride.setPrice(dto.getPrice());
            ride.setStart(now);
            ride.setStatus(RideStatus.Pending);

            long totalMinutes = (estimatedPickupMinutes != null ? estimatedPickupMinutes : 0L)
                    + dto.getEstimatedDuration();
            ride.setEstimatedTimeArrival(now.plusMinutes(totalMinutes));

            ride.setFinish(null);
            ride.setCancellationReason(null);

            ride = rideRepository.save(ride);

            driver.setStatus(DriverStatus.DRIVING);
            driverRepository.save(driver);
        }

        return ride;
    }
}
