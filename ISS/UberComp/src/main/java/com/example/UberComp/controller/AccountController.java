package com.example.UberComp.controller;


import com.example.UberComp.dto.PageDTO;
import com.example.UberComp.dto.driver.DriverChangeRequestDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.dto.user.GetProfileDTO;
import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.account.*;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.Account;
import com.example.UberComp.model.User;
import com.example.UberComp.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAnyAuthority;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "http://localhost:4200")
class AccountController {
    @Autowired
    private AccountService accountService;

    @PreAuthorize("hasAnyAuthority('passenger', 'driver', 'administrator')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver', 'administrator')")
    @PutMapping("/me/change-password")
    public ResponseEntity<?> changePassword(Authentication auth, @Valid @RequestBody ChangePasswordDTO dto) {
        Account account = (Account) auth.getPrincipal();
        String message = accountService.changePassword(account.getId(), dto);
        if (!message.isEmpty())
            return ResponseEntity.ok().body(Map.of("message", message));
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver', 'administrator')")
    @GetMapping("/me")
    public ResponseEntity<GetProfileDTO> getProfile(Authentication auth) throws Exception {
        String email = auth.getName();

        GetProfileDTO profile = accountService.getProfileByEmail(email);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @PreAuthorize("hasAuthority('administrator')")
    @GetMapping("/change-requests")
    public ResponseEntity<List<DriverChangeRequestDTO>> getChangeRequests() {
        List<DriverChangeRequestDTO> requests = accountService.getAllPendingRequests();
        return ResponseEntity.ok(requests);
    }

    @PreAuthorize("hasAuthority('administrator')")
    @PostMapping("/approve-change/{id}")
    public ResponseEntity<String> approveChange(@PathVariable Long id) {
        try {
            accountService.approveDriverChange(id);
            return ResponseEntity.ok("Profile change approved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Profile change request not found");
        }
    }

    @PreAuthorize("hasAuthority('administrator')")
    @PostMapping("/reject-change/{id}")
    public ResponseEntity<String> rejectDriverChange(@PathVariable Long id) {
        try {
            accountService.rejectDriverChange(id);
            return ResponseEntity.ok("Profile change rejected successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Profile change request not found");
        }
    }

    @PreAuthorize("hasAnyAuthority('passenger', 'driver', 'administrator')")
    @PutMapping("/me/profile")
    public ResponseEntity<GetProfileDTO> updateProfile(
            Authentication auth,
            @RequestBody CreateUserDTO updatedUser) {
        try {
            Account account = (Account) auth.getPrincipal();
            Long accountId = account.getId();

            GetProfileDTO updatedProfile = accountService.updateProfile(accountId, updatedUser);
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasAuthority('administrator')")
    @GetMapping("/drivers")
    public ResponseEntity<PageDTO<UserDTO>> getDrivers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        PageDTO<UserDTO> drivers = accountService.getDrivers(page, size);
        return ResponseEntity.ok(drivers);
    }

    @PreAuthorize("hasAuthority('administrator')")
    @GetMapping("/passengers")
    public ResponseEntity<PageDTO<UserDTO>> getPassengers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        PageDTO<UserDTO> passengers = accountService.getPassengers(page, size);
        return ResponseEntity.ok(passengers);
    }

    @PreAuthorize("hasAuthority('administrator')")
    @PutMapping("/block")
    public ResponseEntity<String> blockUser(
            @RequestBody Map<String, String> body) {
        try {
            String reason = body.get("reason");
            String mail = body.get("mail");
            accountService.blockUser(mail, reason);
            return ResponseEntity.ok("User blocked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }

    @PreAuthorize("hasAuthority('administrator')")
    @PutMapping("/unblock")
    public ResponseEntity<String> unblockUser(@RequestBody Map<String, String> body) {
        try {
            String mail = body.get("mail");
            accountService.unblockUser(mail);
            return ResponseEntity.ok("User unblocked successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }
}