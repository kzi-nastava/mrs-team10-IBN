package com.example.UberComp.controller;

import com.example.UberComp.dto.GetVehiclePositionDTO;
import com.example.UberComp.dto.driver.CreateDriverDTO;
import com.example.UberComp.dto.driver.DriverDTO;
import com.example.UberComp.dto.driver.DriverStatusDTO;
import com.example.UberComp.dto.driver.UpdatedStatusDriverDTO;
import com.example.UberComp.service.DriverService;
import jdk.jfr.Registered;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<DriverDTO> register(@RequestBody CreateDriverDTO dto) {
        DriverDTO newDriver = driverService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDriver);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedStatusDriverDTO> updateDriverStatus(@RequestBody DriverStatusDTO status, @PathVariable("id") Long id) {
        UpdatedStatusDriverDTO updatedDriver = new UpdatedStatusDriverDTO(id, status.getStatus());
        return ResponseEntity.ok(updatedDriver);
    }
}
