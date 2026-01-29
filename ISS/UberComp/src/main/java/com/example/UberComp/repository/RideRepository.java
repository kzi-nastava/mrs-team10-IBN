package com.example.UberComp.repository;

import com.example.UberComp.enums.DriverStatus;
import com.example.UberComp.enums.RideStatus;
import com.example.UberComp.model.Driver;
import com.example.UberComp.model.Ride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
""")
    Page<Ride> getRidesAdmin(Pageable pageable);

    @Query("""
    SELECT DISTINCT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    LEFT JOIN FETCH r.passengers
    WHERE r.start BETWEEN :startFrom AND :startTo
""")
    Page<Ride> getRidesAdminWithDateFilter(@Param("startFrom") LocalDateTime startFrom,
                                           @Param("startTo") LocalDateTime startTo,
                                           Pageable pageable);

    @Query("""
    SELECT DISTINCT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    LEFT JOIN FETCH r.passengers
    WHERE r.start >= :startFrom
""")
    Page<Ride> getRidesAdminFromDate(@Param("startFrom") LocalDateTime startFrom,
                                     Pageable pageable);

    @Query("""
    SELECT DISTINCT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    LEFT JOIN FETCH r.passengers
    WHERE r.start <= :startTo
""")
    Page<Ride> getRidesAdminToDate(@Param("startTo") LocalDateTime startTo,
                                   Pageable pageable);

    @Query("""
    SELECT DISTINCT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    LEFT JOIN FETCH r.passengers
    WHERE r.driver.id = :driverId
""")
    Page<Ride> getRidesDriver(@Param("driverId") Long driverId, Pageable pageable);

    @Query("""
    SELECT DISTINCT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    LEFT JOIN FETCH r.passengers
    WHERE r.driver.id = :driverId
    AND r.start BETWEEN :startFrom AND :startTo
""")
    Page<Ride> getRidesDriverWithDateFilter(@Param("driverId") Long driverId,
                                            @Param("startFrom") LocalDateTime startFrom,
                                            @Param("startTo") LocalDateTime startTo,
                                            Pageable pageable);

    @Query("""
    SELECT DISTINCT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    LEFT JOIN FETCH r.passengers
    WHERE r.driver.id = :driverId
    AND r.start >= :startFrom
""")
    Page<Ride> getRidesDriverFromDate(@Param("driverId") Long driverId,
                                      @Param("startFrom") LocalDateTime startFrom,
                                      Pageable pageable);

    @Query("""
    SELECT DISTINCT r FROM Ride r
    JOIN FETCH r.route rt
    JOIN FETCH rt.stations
    LEFT JOIN FETCH r.passengers
    WHERE r.driver.id = :driverId
    AND r.start <= :startTo
""")
    Page<Ride> getRidesDriverToDate(@Param("driverId") Long driverId,
                                    @Param("startTo") LocalDateTime startTo,
                                    Pageable pageable);

    @EntityGraph(attributePaths = {"route", "route.stations", "passengers"})
    @Query("""
    SELECT r FROM Ride r
    JOIN r.passengers p
    WHERE p.id = :userId
""")
    Page<Ride> getRidesPassenger(@Param("userId") Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"route", "route.stations", "passengers"})
    @Query("""
    SELECT r FROM Ride r
    JOIN r.passengers p
    WHERE p.id = :userId 
    AND r.start BETWEEN :startFrom AND :startTo """)
    Page<Ride> getRidesPassengerWithDateFilter(@Param("userId") Long userId,
                                               @Param("startFrom") LocalDateTime startFrom,
                                               @Param("startTo") LocalDateTime startTo,
                                               Pageable pageable);

    @EntityGraph(attributePaths = {"route", "route.stations", "passengers"})
    @Query("""
    SELECT r FROM Ride r
    JOIN r.passengers p
    WHERE p.id = :userId 
    AND r.start >= :startFrom """)
    Page<Ride> getRidesPassengerFromDate(@Param("userId") Long userId,
                                         @Param("startFrom") LocalDateTime startFrom,
                                         Pageable pageable);

    @EntityGraph(attributePaths = {"route", "route.stations", "passengers"})
    @Query("""
    SELECT r FROM Ride r
    JOIN r.passengers p
    WHERE p.id = :userId 
    AND r.start <= :startTo """)
    Page<Ride> getRidesPassengerToDate(@Param("userId") Long userId,
                                       @Param("startTo") LocalDateTime startTo,
                                       Pageable pageable);

    Optional<Ride> findFirstByDriverAndStatusOrderByStartDesc(Driver driver, RideStatus status);
    Optional<Ride> findFirstByDriverAndStatusOrderByStartAsc(Driver driver, RideStatus rideStatus);
    Optional<Ride> findFirstByPassengersIdAndStatusOrderByStartAsc(Long id, RideStatus rideStatus);

    @Query("""
    SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
    FROM Ride r
    JOIN r.passengers p
    WHERE p.id = :userId AND r.status = :status
""")
    boolean existsByPassengerIdAndStatus(@Param("userId") Long userId, @Param("status") RideStatus status);

    List<Ride> findByStatusAndDriverStatus(RideStatus rideStatus, DriverStatus driverStatus);
}



