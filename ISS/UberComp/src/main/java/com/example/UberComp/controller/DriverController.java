package com.example.UberComp.controller;

import com.example.UberComp.dto.GetVehiclePositionDTO;
import com.example.UberComp.service.DriverService;
import jdk.jfr.Registered;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/drivers")
public class DriverController {
    private DriverService driverService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetVehiclePositionDTO>> getVehiclePosition() {
        List<GetVehiclePositionDTO> vehiclePositions = driverService.getVehiclePosition();
        return ResponseEntity.ok(vehiclePositions);
    }
}
