package com.example.UberComp.service;

import com.example.UberComp.dto.GetRideDTO;
import com.example.UberComp.dto.GetRideDetailsDTO;
import com.example.UberComp.repoInterfaces.IRideRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@AllArgsConstructor
@RequiredArgsConstructor
public class RideService {

    private IRideRepository iRideRepository;
    public Collection<GetRideDTO> getRides(Long driverId){
        return java.util.List.of();
    }
    public GetRideDetailsDTO getRide(Long rideId){
        return new GetRideDetailsDTO();
    }
}
