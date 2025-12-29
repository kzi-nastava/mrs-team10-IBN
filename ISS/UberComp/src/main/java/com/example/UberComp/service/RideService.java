package com.example.UberComp.service;

import com.example.UberComp.dto.ride.*;
import com.example.UberComp.repository.RideRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class RideService {

    private RideRepository iRideRepository;
    public Collection<GetRideDTO> getRides(Long driverId){
        return java.util.List.of();
    }
    public GetRideDetailsDTO getRide(Long rideId){
        return new GetRideDetailsDTO();
    }
    public UpdatedStatusRideDTO updateRideStatus(UpdateStatusRideDTO updateRideDTO){ return new UpdatedStatusRideDTO();}
    public GetTrackingRideDTO getTrackingRide(Long rideId){ return new GetTrackingRideDTO();}
}
