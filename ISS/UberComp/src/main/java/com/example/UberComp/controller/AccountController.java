package com.example.UberComp.controller;


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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "http://localhost:4200")
class AccountController {
    @Autowired
    private AccountService accountService;

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedAccountDTO> updateAccount(@RequestBody UpdateAccountDTO account, @PathVariable("id") Long id) throws Exception{
        UpdatedAccountDTO updated = new UpdatedAccountDTO(id, account.getPassword(), account.getAccountStatus(), account.getBlockingReason());
        return new ResponseEntity<UpdatedAccountDTO>(updated, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/me/change-password")
    public ResponseEntity<?> changePassword(Authentication auth, @RequestBody ChangePasswordDTO dto) {
        Account account = (Account) auth.getPrincipal();
        String message = accountService.changePassword(account.getId(), dto);
        if (!message.isEmpty())
            return ResponseEntity.ok().body(Map.of("message", message));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<GetProfileDTO> getProfile(Authentication auth) throws Exception {
        String email = auth.getName();

        GetProfileDTO profile = accountService.getProfileByEmail(email);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/change-requests")
    public ResponseEntity<List<DriverChangeRequestDTO>> getChangeRequests() {
        List<DriverChangeRequestDTO> requests = accountService.getAllPendingRequests();
        return ResponseEntity.ok(requests);
    }

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
}