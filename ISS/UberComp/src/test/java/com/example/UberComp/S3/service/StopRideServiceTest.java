package com.example.UberComp.S3.service;

import com.example.UberComp.dto.ride.StopRideDTO;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import com.example.UberComp.service.RideService;

import com.example.UberComp.utils.EmailUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public class StopRideServiceTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private CoordinateRepository coordinateRepository;
    @Mock
    private EmailUtils emailUtils;
    @InjectMocks
    private RideService rideService;

    private static final Long VALID_ID = 0L;
    private static final Long INVALID_ID = 1L;

    @BeforeMethod
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

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Ride Finish before Ride Start")
    public void testWhenRideFinishIsBeforeRideStart(){
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(10.0, 10.0, "Address 1"));
        coordinates.add(new Coordinate(20.0, 20.0, "Address 2"));
        coordinates.add(new Coordinate(30.0, 30.0, "Address 3"));
        Route route = new Route();
        route.setId(VALID_ID);
        route.setStations(coordinates);

        VehicleType type = new VehicleType(VALID_ID, "Standard", 100.0);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(VALID_ID);
        vehicle.setVehicleType(type);

        Driver driver = new Driver();
        driver.setId(VALID_ID);
        driver.setStatus(DriverStatus.DRIVING);
        driver.setVehicle(vehicle);

        Ride ride = new Ride();
        ride.setId(VALID_ID);
        ride.setRoute(route);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.Ongoing);
        ride.setStart(LocalDateTime.now().minusMinutes(10));
        ride.setDistance(100.0);
        ride.setEstimatedTimeArrival(LocalDateTime.now().plusMinutes(10));

        StopRideDTO stopRideDTO = new StopRideDTO();
        stopRideDTO.setId(VALID_ID);
        stopRideDTO.setPassed(1);
        stopRideDTO.setLat(15.0);
        stopRideDTO.setLon(15.0);
        stopRideDTO.setAddress("Address STOP, Novi Sad");
        stopRideDTO.setDistance(50.0);
        stopRideDTO.setFinishTime(LocalDateTime.now().minusMinutes(20).toString());

        when(rideRepository.findById(VALID_ID)).thenReturn(Optional.of(ride));

        rideService.stopRide(stopRideDTO, false);

        verify(rideRepository).findById(VALID_ID);
        verifyNoMoreInteractions(rideRepository);
        verify(routeRepository).save(route);
        verifyNoMoreInteractions(routeRepository);
        verifyNoInteractions(vehicleRepository);
        verifyNoInteractions(driverRepository);
    }

    @DataProvider(name = "test_params")
    private Object[][] getPassedStations(){
        return new Object[][]{
                {1, DriverStatus.DRIVING, DriverStatus.ONLINE},
                {2, DriverStatus.DRIVING, DriverStatus.ONLINE},
                {1, DriverStatus.OFFLINE_AFTER_RIDE, DriverStatus.OFFLINE}
        };
    }

    @Test(dataProvider = "test_params")
    public void testWhenRideStops(int passed, DriverStatus statusBeforeRide, DriverStatus statusAfterRide){
        List<Coordinate> coordinates = new ArrayList<>();
        coordinates.add(new Coordinate(10.0, 10.0, "Address 1"));
        coordinates.add(new Coordinate(20.0, 20.0, "Address 2"));
        coordinates.add(new Coordinate(30.0, 30.0, "Address 3"));
        Route route = new Route();
        route.setId(VALID_ID);
        route.setStations(coordinates);

        VehicleType type = new VehicleType(VALID_ID, "Standard", 100.0);
        Vehicle vehicle = new Vehicle();
        vehicle.setId(VALID_ID);
        vehicle.setVehicleType(type);

        Driver driver = new Driver();
        driver.setId(VALID_ID);
        driver.setStatus(statusBeforeRide);
        driver.setVehicle(vehicle);

        Ride ride = new Ride();
        ride.setId(VALID_ID);
        ride.setRoute(route);
        ride.setDriver(driver);
        ride.setStatus(RideStatus.Ongoing);
        ride.setStart(LocalDateTime.now().minusMinutes(10));
        ride.setDistance(100.0);
        ride.setEstimatedTimeArrival(LocalDateTime.now().plusMinutes(10));

        StopRideDTO stopRideDTO = new StopRideDTO();
        stopRideDTO.setId(VALID_ID);
        stopRideDTO.setPassed(passed);
        stopRideDTO.setLat(15.0);
        stopRideDTO.setLon(15.0);
        stopRideDTO.setAddress("Address STOP, Novi Sad");
        stopRideDTO.setDistance(50.0);
        stopRideDTO.setFinishTime(LocalDateTime.now().toString());

        when(rideRepository.findById(VALID_ID)).thenReturn(Optional.of(ride));
        when(coordinateRepository.save(any())).thenReturn(new Coordinate(stopRideDTO.getLat(), stopRideDTO.getLon(), stopRideDTO.getAddress()));

        rideService.stopRide(stopRideDTO, false);

        Coordinate lastCoord = ride.getRoute().getStations().get(ride.getRoute().getStations().size() - 1);

        assertEquals(coordinates.subList(0, passed), ride.getRoute().getStations().subList(0, passed));
        assertEquals(stopRideDTO.getAddress(), lastCoord.getAddress());
        assertEquals(stopRideDTO.getLat(), lastCoord.getLat());
        assertEquals(stopRideDTO.getLon(), lastCoord.getLon());
        assertEquals(ride.getStatus(), RideStatus.Finished);
        assertEquals(driver.getStatus(), statusAfterRide);
        assertEquals(vehicle.getLocation(), lastCoord);
        assertEquals(stopRideDTO.getDistance(), ride.getDistance());
        assertEquals(stopRideDTO.getDistance() *  120 + vehicle.getVehicleType().getPrice(), ride.getPrice());

        verify(routeRepository).save(route);
        verify(rideRepository, times(2)).findById(VALID_ID);
        verify(rideRepository).save(ride);
        verify(vehicleRepository).save(vehicle);
        verify(driverRepository).save(driver);
        verifyNoMoreInteractions(routeRepository);
        verifyNoMoreInteractions(rideRepository);
        verifyNoMoreInteractions(driverRepository);
        verifyNoMoreInteractions(vehicleRepository);
    }
}