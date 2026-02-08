package com.example.UberComp.controller;

import com.example.UberComp.dto.user.StatisticsDTO;
import com.example.UberComp.dto.user.UserBasicDTO;
import com.example.UberComp.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "http://localhost:4200")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/my")
    @PreAuthorize("hasAnyAuthority('passenger', 'driver', 'administrator')")
    public ResponseEntity<StatisticsDTO> getMyStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {

        String email = authentication.getName();
        StatisticsDTO statistics = statisticsService.getUserStatisticsByEmail(email, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/all/{userType}")
    @PreAuthorize("hasAuthority('administrator')")
    public ResponseEntity<StatisticsDTO> getAllUsersStatistics(
            @PathVariable String userType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (!userType.equals("drivers") && !userType.equals("passengers")) {
            return ResponseEntity.badRequest().build();
        }

        StatisticsDTO statistics = statisticsService.getAllUsersStatistics(userType, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('administrator')")
    public ResponseEntity<StatisticsDTO> getUserStatistics(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        StatisticsDTO statistics = statisticsService.getUserStatisticsById(userId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/users/drivers")
    @PreAuthorize("hasAuthority('administrator')")
    public ResponseEntity<List<UserBasicDTO>> getAllDrivers() {
        List<UserBasicDTO> drivers = statisticsService.getAllDrivers();
        return ResponseEntity.ok(drivers);
    }

    @GetMapping("/users/passengers")
    @PreAuthorize("hasAuthority('administrator')")
    public ResponseEntity<List<UserBasicDTO>> getAllPassengers() {
        List<UserBasicDTO> passengers = statisticsService.getAllPassengers();
        return ResponseEntity.ok(passengers);
    }
}