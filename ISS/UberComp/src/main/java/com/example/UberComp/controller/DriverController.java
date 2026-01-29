package com.example.UberComp.controller;

import com.example.UberComp.dto.account.RegisterDTO;
import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.driver.*;
import com.example.UberComp.model.Account;
import com.example.UberComp.model.User;
import com.example.UberComp.service.AccountService;
import com.example.UberComp.service.DriverService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/drivers")
public class DriverController {
    @Autowired
    private DriverService driverService;
    @Autowired
    private AccountService accountService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GetVehiclePositionDTO>> getVehiclePositions() {
        List<GetVehiclePositionDTO> vehiclePositions = driverService.getVehiclePositions();
        return ResponseEntity.ok(vehiclePositions);
    }

    @PreAuthorize("hasAuthority('administrator')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody CreateDriverDTO dto) {
        try {
            User user = accountService.register(new RegisterDTO(dto.getAccountDTO(), dto.getCreateUserDTO()));
            DriverDTO newDriver = driverService.register(dto, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newDriver);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error");
        }
    }

    @PreAuthorize("hasAuthority('driver')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedStatusDriverDTO> updateDriverStatus(@RequestBody DriverStatusDTO status, @PathVariable("id") Long id) {
        UpdatedStatusDriverDTO updatedDriver = new UpdatedStatusDriverDTO(id, status.getStatus());
        return ResponseEntity.ok(updatedDriver);
    }

    @PreAuthorize("hasAuthority('driver')")
    @GetMapping("/me")
    public ResponseEntity<DriverDTO> getDriverProfile(Authentication auth) {
        try {
            Account account = (Account) auth.getPrincipal();
            DriverDTO driverDTO = driverService.findByAccountId(account.getId());
            if (driverDTO == null) {
                driverDTO = driverService.findByUserId(account.getUser().getId());
            }
            if (driverDTO == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
            return ResponseEntity.ok(driverDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasAuthority('driver')")
    @PostMapping("/me/change-request")
    public ResponseEntity<Void> submitDriverChangeRequest(
            Authentication auth,
            @RequestBody UpdateDriverDTO changeRequest) {
        try {
            Account account = (Account) auth.getPrincipal();
            Long userId = account.getUser().getId();

            driverService.submitDriverChangeRequest(userId, changeRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasAuthority('driver')")
    @PutMapping("/me/update-location")
    public ResponseEntity<Void> updateLocation(
            Authentication auth,
            @RequestBody String address) {
        try {
            Account account = (Account) auth.getPrincipal();
            Long userId = account.getUser().getId();
            driverService.updateDriverLocation(userId, address);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}