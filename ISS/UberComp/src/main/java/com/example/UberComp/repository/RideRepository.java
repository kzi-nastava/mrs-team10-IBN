package com.example.UberComp.repository;

import com.example.UberComp.model.Ride;
import com.example.UberComp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, Long> {

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
    SELECT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    LEFT JOIN FETCH r.passengers
""")
    List<Ride> getRidesWithRouteAndStations();

}



