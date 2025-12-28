package com.example.UberComp.controller;

import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.service.DriverService;
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

    @GetMapping("/{id}")
    public ResponseEntity<DriverDTO> getDriverProfile(@PathVariable Long id) {
        AccountDTO accountDTO = new AccountDTO("driver@gmail.com");

        CreateUserDTO createUserDTO = new CreateUserDTO("Bojana", "Paunović", "adresa", "061234567", "image.png");

        VehicleDTO vehicleDTO = new VehicleDTO(new VehicleTypeDTO(), "Tesla Model 3", "NS123-XY", 4, true, true);

        DriverDTO profile = new DriverDTO(accountDTO, createUserDTO, vehicleDTO, 8);

        return ResponseEntity.ok(profile);
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
