package com.example.UberComp.S2.service;

import com.example.UberComp.dto.ride.RideMomentDTO;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.DriverRepository;
import com.example.UberComp.repository.RideRepository;
import com.example.UberComp.repository.VehicleRepository;
import com.example.UberComp.service.RideService;
import com.example.UberComp.utils.EmailUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class FinishRideServiceTest {
    @Mock
    RideRepository rideRepository;
    @Mock
    VehicleRepository vehicleRepository;
    @Mock
    DriverRepository driverRepository;
    @Mock
    EmailUtils emailUtils;
    @InjectMocks
    RideService rideService;
    private final Long VALID_ID = 1L;

    @BeforeMethod
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test(expectedExceptions = NoSuchElementException.class, expectedExceptionsMessageRegExp = "Ride not found")
    void test_end_ride_when_id_is_invalid(){
        RideMomentDTO dto = new RideMomentDTO();
        Long INVALID_ID = -1L;
        when(rideRepository.findById(INVALID_ID)).thenReturn(Optional.empty());
        rideService.endRide(INVALID_ID, dto);
        Mockito.doNothing().when(emailUtils).sendEmailWhenRideIsFinished("ignjaticivana70@gmail.com", INVALID_ID);


        Mockito.verify(rideRepository);
        Mockito.verifyNoInteractions(vehicleRepository);
        Mockito.verifyNoInteractions(driverRepository);
    }

    @Test
    void test_when_driver_is_offline_after_ride() {
        Ride ride = new Ride();
        Driver driver = new Driver();
        driver.setStatus(DriverStatus.OFFLINE_AFTER_RIDE);

        Vehicle vehicle = new Vehicle();
        driver.setVehicle(vehicle);
        ride.setDriver(driver);

        Route route = new Route();
        route.setStations(List.of(new Coordinate(), new Coordinate()));
        ride.setRoute(route);

        when(rideRepository.findById(VALID_ID))
                .thenReturn(Optional.of(ride));
        Mockito.doNothing().when(emailUtils).sendEmailWhenRideIsFinished("ignjaticivana70@gmail.com", VALID_ID);


        RideMomentDTO dto = new RideMomentDTO();
        dto.setIsotime("2026-10-10T12:15:00");

        rideService.endRide(VALID_ID, dto);

        assertEquals(DriverStatus.OFFLINE, driver.getStatus());
        assertEquals(RideStatus.Finished, ride.getStatus());
        Mockito.verify(vehicleRepository).save(vehicle);
        Mockito.verify(driverRepository).save(driver);
    }

    @Test
    void test_when_driver_is_online_after_ride() {
        Ride ride = new Ride();
        Driver driver = new Driver();
        driver.setStatus(DriverStatus.DRIVING);

        Vehicle vehicle = new Vehicle();
        driver.setVehicle(vehicle);
        ride.setDriver(driver);

        Route route = new Route();
        route.setStations(List.of(new Coordinate(), new Coordinate()));
        ride.setRoute(route);

        when(rideRepository.findById(VALID_ID))
                .thenReturn(Optional.of(ride));
        Mockito.doNothing().when(emailUtils).sendEmailWhenRideIsFinished("ignjaticivana70@gmail.com", VALID_ID);


        RideMomentDTO dto = new RideMomentDTO();
        dto.setIsotime("2026-10-10T12:15:00");

        rideService.endRide(VALID_ID, dto);

        assertEquals(DriverStatus.ONLINE, driver.getStatus());
        assertEquals(RideStatus.Finished, ride.getStatus());

        Mockito.verify(vehicleRepository).save(vehicle);
        Mockito.verify(driverRepository).save(driver);
    }

    @Test
    void test_end_ride_sends_email() {
        Ride ride = new Ride();
        Driver driver = new Driver();
        driver.setStatus(DriverStatus.ONLINE);
        Vehicle vehicle = new Vehicle();
        driver.setVehicle(vehicle);
        ride.setDriver(driver);

        Route route = new Route();
        route.setStations(List.of(new Coordinate(), new Coordinate()));
        ride.setRoute(route);

        Mockito.when(rideRepository.findById(VALID_ID))
                .thenReturn(Optional.of(ride));

        RideMomentDTO dto = new RideMomentDTO();
        dto.setIsotime("2026-10-10T12:15:00");

        rideService.endRide(VALID_ID, dto);

        Mockito.verify(emailUtils)
                .sendEmailWhenRideIsFinished("ignjaticivana70@gmail.com", VALID_ID);
    }


}
