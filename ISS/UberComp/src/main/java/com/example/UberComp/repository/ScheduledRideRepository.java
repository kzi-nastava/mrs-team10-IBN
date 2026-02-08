package com.example.UberComp.repository;

import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.Driver;
import com.example.UberComp.model.Ride;
import com.example.UberComp.model.ScheduledRide;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {
            "route",
            "route.stations",
            "passengers"
    })
    @Query(
            value = """
        SELECT r FROM ScheduledRide r
        WHERE r.driver.id = :driverId
        AND r.start > CURRENT_TIMESTAMP
        ORDER BY r.start ASC
    """,
            countQuery = """
        SELECT COUNT(r) FROM ScheduledRide r
        WHERE r.driver.id = :driverId
        AND r.start > CURRENT_TIMESTAMP
    """
    )
    Page<ScheduledRide> getScheduledRidesForDriver(
            @Param("driverId") Long driverId,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "passengers",
            "passengers.account"
    })
    @Query("""
    SELECT sr FROM ScheduledRide sr
    JOIN FETCH sr.driver
    WHERE sr.status = com.example.UberComp.enums.RideStatus.Pending
      AND sr.scheduled > :now
    ORDER BY sr.scheduled ASC
""")
    List<ScheduledRide> findPendingScheduledRides(@Param("now") LocalDateTime nowPlus5);

}
