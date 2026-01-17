package com.example.UberComp.repository;

import com.example.UberComp.model.Driver;
import com.example.UberComp.model.ScheduledRide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledRideRepository extends JpaRepository<ScheduledRide, Long> {

    @Query("""
        SELECT sr FROM ScheduledRide sr
        JOIN FETCH sr.route r
        JOIN FETCH r.stations
        WHERE sr.driver = :driver
        AND sr.scheduled BETWEEN :start AND :end
        ORDER BY sr.scheduled ASC
    """)
    List<ScheduledRide> findByDriverAndScheduledBetweenOrderByScheduledAsc(
            @Param("driver") Driver driver,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}