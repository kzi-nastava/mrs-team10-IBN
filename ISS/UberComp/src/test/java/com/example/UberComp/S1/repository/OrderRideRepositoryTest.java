package com.example.UberComp.S1.repository;

import com.example.UberComp.enums.AccountType;
import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.*;
import com.example.UberComp.repository.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRideRepositoryTest {

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ScheduledRideRepository scheduledRideRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Test
    void shouldFindDriversByStatus() {
        List<Driver> drivers = driverRepository.findByStatus(DriverStatus.ONLINE);

        assertFalse(drivers.isEmpty());
        assertEquals(5, drivers.size());
    }

    @Test
    void shouldReturnEmptyWhenNoDriversWithGivenStatus() {
        List<Driver> drivers = driverRepository.findByStatus(DriverStatus.OFFLINE);

        assertTrue(drivers.isEmpty());
    }


    @Test
    void shouldFindUserByEmail() {
        Optional<User> user = userRepository.findByAccountEmail("passenger@mail.com");

        assertTrue(user.isPresent());
        assertEquals("Jane", user.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenUserEmailDoesNotExist() {
        Optional<User> user = userRepository.findByAccountEmail("unknown@mail.com");

        assertTrue(user.isEmpty());
    }


    @Test
    void shouldFindVehicleByPlate() {
        Vehicle vehicle = vehicleRepository.findByPlate("BG-123-AB");

        assertNotNull(vehicle);
        assertEquals("Toyota Corolla", vehicle.getModel());
    }

    @Test
    void shouldReturnNullWhenVehiclePlateDoesNotExist() {
        Vehicle vehicle = vehicleRepository.findByPlate("XX-000-XX");

        assertNull(vehicle);
    }

    @Test
    void shouldFindAccountByEmail() {
        Account account = accountRepository.findByEmail("driver@mail.com");

        assertNotNull(account);
        assertEquals(AccountType.DRIVER, account.getAccountType());
    }

    @Test
    void shouldReturnNullWhenAccountEmailDoesNotExist() {
        Account account = accountRepository.findByEmail("none@mail.com");

        assertNull(account);
    }

    @Test
    void shouldFindScheduledRidesForDriverBetweenDates() {

        Driver driver = driverRepository.findById(1L).orElseThrow();
        Route route = routeRepository.findById(1L).orElseThrow();

        ScheduledRide ride = new ScheduledRide();

        ride.setDriver(driver);
        ride.setRoute(route);
        ride.setStart(LocalDateTime.of(2026, 2, 12, 12, 0));
        ride.setEstimatedTimeArrival(LocalDateTime.of(2026, 2, 12, 12, 20));
        ride.setStatus(RideStatus.Pending);
        ride.setPrice(200.0);
        ride.setDistance(5.0);
        ride.setBabies(false);
        ride.setPets(true);

        ride.setScheduled(LocalDateTime.of(2026, 2, 12, 12, 0));
        ride.setReminder15Sent(false);
        ride.setReminder10Sent(false);
        ride.setReminder5Sent(false);
        ride.setDriverShouldLeaveAt(null);
        ride.setDriverNotified(false);


        scheduledRideRepository.save(ride);

        List<ScheduledRide> result =
                scheduledRideRepository.findByDriverAndScheduledBetweenOrderByScheduledAsc(
                        driver,
                        LocalDateTime.of(2026, 2, 11, 0, 0),
                        LocalDateTime.of(2026, 2, 13, 0, 0)
                );

        assertEquals(1, result.size());
        assertEquals(LocalDateTime.of(2026, 2, 12, 12, 0),
                result.get(0).getScheduled());
    }

    @Test
    void shouldReturnEmptyWhenNoScheduledRidesInRange() {

        Driver driver = driverRepository.findById(1L).orElseThrow();

        List<ScheduledRide> result =
                scheduledRideRepository.findByDriverAndScheduledBetweenOrderByScheduledAsc(
                        driver,
                        LocalDateTime.of(2025, 1, 1, 0, 0),
                        LocalDateTime.of(2025, 1, 2, 0, 0)
                );

        assertTrue(result.isEmpty());
    }
}
