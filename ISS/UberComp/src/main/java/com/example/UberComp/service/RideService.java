package com.example.UberComp.service;

import com.example.UberComp.dto.driver.GetVehiclePositionDTO;
import com.example.UberComp.dto.ride.*;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.Coordinate;
import com.example.UberComp.model.Driver;
import com.example.UberComp.model.Ride;
import com.example.UberComp.model.Route;
import com.example.UberComp.repository.CoordinateRepository;
import com.example.UberComp.repository.DriverRepository;
import com.example.UberComp.repository.RideRepository;
import com.example.UberComp.repository.RouteRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class RideService {

    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private CoordinateRepository coordinateRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private DriverRepository driverRepository;

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

    public Collection<GetRideDTO> getRides(Long driverId){
        return rideRepository.getRidesWithRouteAndStations()
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
            Ride ride = rideRepository.findFirstByDriver_IdOrderByStart(driver.getId());
            activeRides.add(new GetVehiclePositionDTO(ride, true));
        }
        activeDrivers = driverRepository.findByStatus(DriverStatus.ONLINE);
        for(Driver driver: activeDrivers) {
            Ride ride = rideRepository.findFirstByDriver_IdOrderByStart(driver.getId());
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
}
