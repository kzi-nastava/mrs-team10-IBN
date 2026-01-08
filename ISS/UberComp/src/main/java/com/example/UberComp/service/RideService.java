package com.example.UberComp.service;

import com.example.UberComp.dto.driver.GetVehiclePositionDTO;
import com.example.UberComp.dto.ride.*;
import com.example.UberComp.model.Ride;
import com.example.UberComp.repository.RideRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.transaction.annotation.Transactional;


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
    @Transactional(readOnly = true)
    public GetRideDetailsDTO getRide(Long rideId){
        Ride ride = rideRepository.getRideWithRoute(rideId);
        ride = rideRepository.getRideWithPassengers(rideId);
        return new GetRideDetailsDTO(ride);
    }

    @Transactional(readOnly = true)
    public ArrayList<GetVehiclePositionDTO> getActiveRides() {
        //get active drivers
        Ride ride = rideRepository.findFirstByDriver_IdOrderByStart(1L);
        ArrayList<GetVehiclePositionDTO> activeRides = new ArrayList<>();
        activeRides.add(new GetVehiclePositionDTO(ride));
        return activeRides;
    }

    public UpdatedStatusRideDTO updateRideStatus(UpdateStatusRideDTO updateRideDTO){ return new UpdatedStatusRideDTO();}
    public GetTrackingRideDTO getTrackingRide(Long rideId){ return new GetTrackingRideDTO();}
}
