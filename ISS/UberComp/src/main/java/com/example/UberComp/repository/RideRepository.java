package com.example.UberComp.repository;

import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.Driver;
import com.example.UberComp.model.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RideRepository extends JpaRepository<Ride, Long> {

    Ride findFirstByDriver_IdOrderByStartDesc(Long driverId);
    Ride findFirstByPassengersIdOrderByStartDesc(Long passengersId);

    @Query("""
    SELECT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    WHERE r.id = :rideId
""")
    Ride getRideWithRoute(Long rideId);

    @Query("""
    SELECT r FROM Ride r
    LEFT JOIN FETCH r.passengers
    WHERE r.id = :rideId
""")
    Ride getRideWithPassengers(Long rideId);

    @Query("""
    SELECT DISTINCT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    LEFT JOIN FETCH r.passengers
    WHERE r.driver.id = :driverId
""")
    List<Ride> getRidesDriver(@Param("driverId") Long driverId);

    @Query("""
        SELECT DISTINCT r FROM Ride r
        JOIN FETCH r.passengers p
        JOIN FETCH r.route rt
        JOIN FETCH rt.stations
        WHERE p.id = :userId
        """)
    List<Ride> getRidesPassenger(@Param("userId") Long userId);

    Optional<Ride> findFirstByDriverAndStatusOrderByStartDesc(Driver driver, RideStatus status);
}



