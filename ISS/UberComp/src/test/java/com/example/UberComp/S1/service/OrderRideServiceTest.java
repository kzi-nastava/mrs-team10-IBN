package com.example.UberComp.S1.service;

import com.example.UberComp.dto.driver.AvailableDriverDTO;
import com.example.UberComp.dto.driver.DriverDTO;
import com.example.UberComp.dto.ride.CreateRideDTO;
import com.example.UberComp.dto.driver.GetCoordinateDTO;
import com.example.UberComp.dto.ride.RideOrderResponseDTO;
import com.example.UberComp.dto.vehicle.VehicleLocationDTO;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;
import com.example.UberComp.service.DriverService;
import com.example.UberComp.service.NotificationService;
import com.example.UberComp.service.RideService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class OrderRideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private CoordinateRepository coordinateRepository;

    @Mock
    private ScheduledRideRepository scheduledRideRepository;

    @Mock
    private VehicleTypeRepository vehicleTypeRepository;

    @Mock
    private DriverService driverService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RideService rideService;

    private Driver driver;
    private User passenger;
    private VehicleType vehicleType;
    private CreateRideDTO createRideDTO;
    private AvailableDriverDTO availableDriverDTO;
    private Coordinate startCoordinate;
    private Coordinate destinationCoordinate;

    @BeforeMethod
    void setup() {
        MockitoAnnotations.openMocks(this);
        setupTestData();
    }

    private void setupTestData() {
        vehicleType = new VehicleType();
        vehicleType.setId(1L);
        vehicleType.setName("STANDARD");
        vehicleType.setPrice(100.0);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setModel("Toyota Corolla");
        vehicle.setPlate("BG-123-AB");
        vehicle.setVehicleType(vehicleType);
        vehicle.setBabySeat(false);
        vehicle.setPetFriendly(false);

        Account driverAccount = new Account();
        driverAccount.setId(1L);
        driverAccount.setEmail("driver@mail.com");
        driverAccount.setAccountStatus(AccountStatus.VERIFIED);

        driver = new Driver();
        driver.setId(1L);
        driver.setName("John");
        driver.setLastName("Doe");
        driver.setPhone("+381234567890");
        driver.setAccount(driverAccount);
        driver.setVehicle(vehicle);
        driver.setStatus(DriverStatus.ONLINE);
        driver.setHomeAddress("Bulevar Oslobodjenja 1, Novi Sad");

        vehicle.setDriver(driver);
        driverAccount.setUser(driver);

        Account passengerAccount = new Account();
        passengerAccount.setId(2L);
        passengerAccount.setEmail("passenger@mail.com");
        passengerAccount.setAccountStatus(AccountStatus.VERIFIED);

        passenger = new User();
        passenger.setId(2L);
        passenger.setName("Jane");
        passenger.setLastName("Smith");
        passenger.setAccount(passengerAccount);
        passengerAccount.setUser(passenger);

        startCoordinate = new Coordinate();
        startCoordinate.setId(1L);
        startCoordinate.setLat(45.2396);
        startCoordinate.setLon(19.8227);
        startCoordinate.setAddress("Bulevar Oslobodjenja 1, Novi Sad");

        destinationCoordinate = new Coordinate();
        destinationCoordinate.setId(2L);
        destinationCoordinate.setLat(45.2551);
        destinationCoordinate.setLon(19.8451);
        destinationCoordinate.setAddress("Narodnih heroja 14, Novi Sad");

        createRideDTO = new CreateRideDTO();

        GetCoordinateDTO startDTO = new GetCoordinateDTO();
        startDTO.setAddress("Bulevar Oslobodjenja 1, Novi Sad");
        startDTO.setLat(45.2396);
        startDTO.setLon(19.8227);
        createRideDTO.setStartAddress(startDTO);

        GetCoordinateDTO destDTO = new GetCoordinateDTO();
        destDTO.setAddress("Narodnih heroja 14, Novi Sad");
        destDTO.setLat(45.2551);
        destDTO.setLon(19.8451);
        createRideDTO.setDestinationAddress(destDTO);

        createRideDTO.setStops(new ArrayList<>());
        createRideDTO.setVehicleType("STANDARD");
        createRideDTO.setBabySeat(false);
        createRideDTO.setPetFriendly(false);
        createRideDTO.setScheduled(null);
        createRideDTO.setPrice(500.0);
        createRideDTO.setDistance(5.0);
        createRideDTO.setEstimatedDuration(15);

        VehicleLocationDTO vehicleLocation = new VehicleLocationDTO(
                45.2400,
                19.8230,
                "Bulevar Oslobodjenja 5, Novi Sad"
        );

        availableDriverDTO = new AvailableDriverDTO(
                new DriverDTO(driver),
                5L,
                vehicleLocation,
                null
        );
    }

    @Test
    public void test_create_ride_successfully() {
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(userRepository.findById(passenger.getId())).thenReturn(Optional.of(passenger));
        when(coordinateRepository.findByLatAndLon(startCoordinate.getLat(), startCoordinate.getLon()))
                .thenReturn(Optional.of(startCoordinate));
        when(coordinateRepository.findByLatAndLon(destinationCoordinate.getLat(), destinationCoordinate.getLon()))
                .thenReturn(Optional.of(destinationCoordinate));

        Route savedRoute = new Route();
        savedRoute.setId(1L);
        savedRoute.setStations(Arrays.asList(startCoordinate, destinationCoordinate));
        when(routeRepository.save(any(Route.class))).thenReturn(savedRoute);

        Ride savedRide = new Ride();
        savedRide.setId(1L);
        savedRide.setRoute(savedRoute);
        savedRide.setDriver(driver);
        savedRide.setPassengers(Set.of(passenger));
        savedRide.setPrice(createRideDTO.getPrice());
        savedRide.setStatus(RideStatus.Pending);
        savedRide.setBabies(false);
        savedRide.setPets(false);
        savedRide.setStart(LocalDateTime.now());
        savedRide.setDistance(createRideDTO.getDistance());
        savedRide.setEstimatedTimeArrival(LocalDateTime.now().plusMinutes(20));

        when(rideRepository.saveAndFlush(any(Ride.class))).thenReturn(savedRide);
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        doNothing().when(notificationService).sendImmediateRideNotifications(
                any(Ride.class), any(User.class), anyLong());

        Ride result = rideService.createRide(
                createRideDTO,
                passenger.getId(),
                driver.getId(),
                5L,
                null
        );

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(RideStatus.Pending, result.getStatus());
        assertEquals(createRideDTO.getPrice(), result.getPrice());
        assertEquals(driver, result.getDriver());
        assertTrue(result.getPassengers().contains(passenger));

        verify(driverRepository).findById(driver.getId());
        verify(userRepository).findById(passenger.getId());
        verify(routeRepository).save(any(Route.class));
        verify(rideRepository).saveAndFlush(any(Ride.class));
        verify(driverRepository).save(driver);
        verify(notificationService).sendImmediateRideNotifications(
                any(Ride.class), eq(passenger), eq(5L));

        assertEquals(DriverStatus.DRIVING, driver.getStatus());
    }

    @Test(expectedExceptions = RuntimeException.class,
            expectedExceptionsMessageRegExp = "Driver not found with id: .*")
    public void test_create_ride_driver_not_found() {
        when(driverRepository.findById(anyLong())).thenReturn(Optional.empty());

        rideService.createRide(
                createRideDTO,
                passenger.getId(),
                999L,
                5L,
                null
        );
    }

    @Test(expectedExceptions = RuntimeException.class,
            expectedExceptionsMessageRegExp = "Passenger not found with id: .*")
    public void test_create_ride_passenger_not_found() {
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        rideService.createRide(
                createRideDTO,
                999L,
                driver.getId(),
                5L,
                null
        );
    }

    @Test
    public void test_create_scheduled_ride() {
        LocalDateTime scheduledTime = LocalDateTime.now().plusHours(2);
        createRideDTO.setScheduled(scheduledTime);

        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(userRepository.findById(passenger.getId())).thenReturn(Optional.of(passenger));
        when(coordinateRepository.findByLatAndLon(startCoordinate.getLat(), startCoordinate.getLon()))
                .thenReturn(Optional.of(startCoordinate));
        when(coordinateRepository.findByLatAndLon(destinationCoordinate.getLat(), destinationCoordinate.getLon()))
                .thenReturn(Optional.of(destinationCoordinate));

        Route savedRoute = new Route();
        savedRoute.setId(1L);
        savedRoute.setStations(Arrays.asList(startCoordinate, destinationCoordinate));
        when(routeRepository.save(any(Route.class))).thenReturn(savedRoute);

        ScheduledRide savedScheduledRide = new ScheduledRide();
        savedScheduledRide.setId(1L);
        savedScheduledRide.setScheduled(scheduledTime);
        savedScheduledRide.setRoute(savedRoute);
        savedScheduledRide.setDriver(driver);
        savedScheduledRide.setPassengers(Set.of(passenger));
        savedScheduledRide.setPrice(createRideDTO.getPrice());
        savedScheduledRide.setStatus(RideStatus.Pending);

        when(rideRepository.save(any(ScheduledRide.class))).thenReturn(savedScheduledRide);

        doNothing().when(notificationService).sendScheduledRideNotifications(
                any(ScheduledRide.class), any(User.class));

        Ride result = rideService.createRide(
                createRideDTO,
                passenger.getId(),
                driver.getId(),
                5L,
                scheduledTime.minusMinutes(10)
        );

        assertNotNull(result);
        assertTrue(result instanceof ScheduledRide);
        assertEquals(scheduledTime, ((ScheduledRide) result).getScheduled());

        verify(rideRepository).save(any(ScheduledRide.class));
        verify(notificationService).sendScheduledRideNotifications(
                any(ScheduledRide.class), eq(passenger));
        verify(driverRepository, never()).save(driver);
    }

    @Test
    public void test_create_ride_with_stops() {
        Coordinate stopCoordinate = new Coordinate();
        stopCoordinate.setId(3L);
        stopCoordinate.setLat(45.2500);
        stopCoordinate.setLon(19.8300);
        stopCoordinate.setAddress("Dunavska 1, Novi Sad");

        GetCoordinateDTO stopDTO = new GetCoordinateDTO();
        stopDTO.setAddress("Dunavska 1, Novi Sad");
        stopDTO.setLat(45.2500);
        stopDTO.setLon(19.8300);

        createRideDTO.setStops(Arrays.asList(stopDTO));

        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(userRepository.findById(passenger.getId())).thenReturn(Optional.of(passenger));
        when(coordinateRepository.findByLatAndLon(startCoordinate.getLat(), startCoordinate.getLon()))
                .thenReturn(Optional.of(startCoordinate));
        when(coordinateRepository.findByLatAndLon(stopCoordinate.getLat(), stopCoordinate.getLon()))
                .thenReturn(Optional.of(stopCoordinate));
        when(coordinateRepository.findByLatAndLon(destinationCoordinate.getLat(), destinationCoordinate.getLon()))
                .thenReturn(Optional.of(destinationCoordinate));

        Route savedRoute = new Route();
        savedRoute.setId(1L);
        savedRoute.setStations(Arrays.asList(startCoordinate, stopCoordinate, destinationCoordinate));
        when(routeRepository.save(any(Route.class))).thenReturn(savedRoute);

        Ride savedRide = new Ride();
        savedRide.setId(1L);
        savedRide.setRoute(savedRoute);
        savedRide.setDriver(driver);
        savedRide.setPassengers(Set.of(passenger));
        savedRide.setPrice(createRideDTO.getPrice());
        savedRide.setStatus(RideStatus.Pending);

        when(rideRepository.saveAndFlush(any(Ride.class))).thenReturn(savedRide);
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        doNothing().when(notificationService).sendImmediateRideNotifications(
                any(Ride.class), any(User.class), anyLong());

        Ride result = rideService.createRide(
                createRideDTO,
                passenger.getId(),
                driver.getId(),
                5L,
                null
        );

        assertNotNull(result);
        assertEquals(3, result.getRoute().getStations().size());
        verify(coordinateRepository, times(3)).findByLatAndLon(any(), any());
    }

    @Test
    public void test_create_ride_with_baby_seat_and_pets() {
        createRideDTO.setBabySeat(true);
        createRideDTO.setPetFriendly(true);

        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(userRepository.findById(passenger.getId())).thenReturn(Optional.of(passenger));
        when(coordinateRepository.findByLatAndLon(any(), any()))
                .thenReturn(Optional.of(startCoordinate))
                .thenReturn(Optional.of(destinationCoordinate));

        Route savedRoute = new Route();
        savedRoute.setId(1L);
        when(routeRepository.save(any(Route.class))).thenReturn(savedRoute);

        Ride savedRide = new Ride();
        savedRide.setId(1L);
        savedRide.setRoute(savedRoute);
        savedRide.setBabies(true);
        savedRide.setPets(true);

        when(rideRepository.saveAndFlush(any(Ride.class))).thenReturn(savedRide);
        when(driverRepository.save(any(Driver.class))).thenReturn(driver);

        doNothing().when(notificationService).sendImmediateRideNotifications(
                any(Ride.class), any(User.class), anyLong());

        Ride result = rideService.createRide(
                createRideDTO,
                passenger.getId(),
                driver.getId(),
                5L,
                null
        );

        assertNotNull(result);
        assertTrue(result.getBabies());
        assertTrue(result.getPets());
    }

    @Test
    public void test_build_ride_order_response() {
        Ride ride = new Ride();
        ride.setId(1L);
        ride.setPrice(500.0);
        ride.setStatus(RideStatus.Pending);

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(driverRepository.findById(driver.getId())).thenReturn(Optional.of(driver));
        when(scheduledRideRepository.findById(1L)).thenReturn(Optional.empty());

        VehicleLocationDTO vehicleLocation = new VehicleLocationDTO(
                45.2400, 19.8230, "Bulevar Oslobodjenja 5, Novi Sad");

        AvailableDriverDTO availableDriver = new AvailableDriverDTO(
                new DriverDTO(driver),
                5L,
                vehicleLocation,
                null
        );

        RideOrderResponseDTO response = rideService.buildRideOrderResponse(1L, availableDriver);

        assertNotNull(response);
        assertEquals(1L, response.getRideId());
        assertEquals(500.0, response.getPrice());
        assertEquals("Pending", response.getStatus());
        assertEquals("John Doe", response.getDriverName());
        assertEquals("+381234567890", response.getDriverPhone());
        assertEquals("Toyota Corolla", response.getVehicleModel());
        assertEquals("BG-123-AB", response.getVehiclePlate());
        assertEquals(5L, response.getEstimatedPickupMinutes());
        assertNotNull(response.getEstimatedPickupTime());

        verify(rideRepository).findById(1L);
        verify(driverRepository).findById(driver.getId());
    }
}