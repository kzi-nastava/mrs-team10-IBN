package com.example.UberComp.service;

import com.example.UberComp.dto.ride.*;
import com.example.UberComp.model.Ride;
import com.example.UberComp.repository.RideRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class RideService {

    @Autowired
    private RideRepository rideRepository;

    public Collection<GetRideDTO> getRides(Long driverId){
        return rideRepository.getRidesWithRouteAndStations()
                .stream()
                .map(GetRideDTO::new)
                .toList();
    }
    public GetRideDetailsDTO getRide(Long rideId){
        return new GetRideDetailsDTO(rideRepository.getRideWithRouteAndStations(rideId));
    }
    public UpdatedStatusRideDTO updateRideStatus(UpdateStatusRideDTO updateRideDTO){ return new UpdatedStatusRideDTO();}
    public GetTrackingRideDTO getTrackingRide(Long rideId){ return new GetTrackingRideDTO();}
}
