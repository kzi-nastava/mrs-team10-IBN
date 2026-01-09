package com.example.UberComp.service;

import com.example.UberComp.dto.ride.*;
import com.example.UberComp.model.Coordinate;
import com.example.UberComp.model.Ride;
import com.example.UberComp.model.Route;
import com.example.UberComp.repository.RideRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public StartRideDTO getIncomingRide(){
        StartRideDTO newRide = new StartRideDTO();
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
    public UpdatedStatusRideDTO updateRideStatus(UpdateStatusRideDTO updateRideDTO){ return new UpdatedStatusRideDTO();}
    public GetTrackingRideDTO getTrackingRide(Long rideId){ return new GetTrackingRideDTO();}

    public void stopRide(StopRideDTO stopRideDTO){

    }
}
