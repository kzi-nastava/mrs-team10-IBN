package com.example.UberComp.S3.service;

import com.example.UberComp.dto.ride.StopRideDTO;
import com.example.UberComp.repository.DriverRepository;
import com.example.UberComp.repository.RideRepository;
import com.example.UberComp.repository.RouteRepository;
import com.example.UberComp.repository.VehicleRepository;
import com.example.UberComp.service.RideService;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;


import java.util.Optional;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class StopRideServiceTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private DriverRepository driverRepository;
    @InjectMocks
    private RideService rideService;

    private static final Long VALID_ID = 0L;
    private static final Long INVALID_ID = 1L;

    @BeforeTest
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }


    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Ride not found")
    public void testWhenRideDoesntExist(){
        when(rideRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        StopRideDTO stopRideDTO = new StopRideDTO();
        stopRideDTO.setId(INVALID_ID);

        rideService.stopRide(stopRideDTO, false);

        verifyNoInteractions(rideRepository);
        verifyNoInteractions(routeRepository);
        verifyNoInteractions(vehicleRepository);
        verifyNoInteractions(driverRepository);
    }
}
