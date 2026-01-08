package com.example.UberComp.controller;

import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/drivers")
public class DriverController {
    @Autowired
    private DriverService driverService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetVehiclePositionDTO>> getVehiclePositions() {
        List<GetVehiclePositionDTO> vehiclePositions = driverService.getVehiclePositions();
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

    @GetMapping("/user/{userId}")
    public ResponseEntity<DriverDTO> getDriverProfile(@PathVariable Long userId) {
        DriverDTO driverDTO = driverService.findById(userId);
        if (driverDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(driverDTO);
    }


    @PutMapping("/{id}/profile")
    public ResponseEntity<DriverDTO> updateDriverProfile(
            @PathVariable Long id,
            @RequestBody UpdateDriverDTO updatedDriverDTO) {

        DriverDTO updatedProfile = new DriverDTO(
                new AccountDTO("driver@gmail.com"),
                updatedDriverDTO.getCreateUserDTO(),
                updatedDriverDTO.getVehicleDTO(),
                8
        );

        return ResponseEntity.ok(updatedProfile);
    }

}
