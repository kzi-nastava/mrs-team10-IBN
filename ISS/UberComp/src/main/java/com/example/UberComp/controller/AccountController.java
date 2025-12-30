package com.example.UberComp.controller;


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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/account")
class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<GetAccountDTO>> getAccounts(){
        ArrayList<GetAccountDTO> accounts = new ArrayList<GetAccountDTO>();

        GetAccountDTO acc1 = new GetAccountDTO("user@gmail.com", AccountType.PASSENGER, AccountStatus.UNVERIFIED, null);
        GetAccountDTO acc2 = new GetAccountDTO("niksa@gmail.com", AccountType.PASSENGER, AccountStatus.VERIFIED, null);

        accounts.add(acc1);
        accounts.add(acc2);

        return new ResponseEntity<Collection<GetAccountDTO>>(accounts,HttpStatus.OK);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAccountDTO> getAccount(@RequestBody LogAccountDTO creds) throws Exception{
        Account loggedIn = accountService.login(creds);
        if(loggedIn == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        GetAccountDTO account = new GetAccountDTO(loggedIn);
        return new ResponseEntity<GetAccountDTO>(account, HttpStatus.OK);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileDTO> createAccount(@RequestBody RegisterDTO userData) throws Exception{
        User registered = accountService.register(userData);
        AccountDTO registeredDTO = new AccountDTO(userData.getEmail());
        CreatedUserDTO createdUserDTO = new CreatedUserDTO(registered);
        GetProfileDTO profile = new GetProfileDTO(createdUserDTO, registeredDTO);
        return new ResponseEntity<GetProfileDTO>(profile, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdatedAccountDTO> updateAccount(@RequestBody UpdateAccountDTO account, @PathVariable("id") Long id) throws Exception{
        UpdatedAccountDTO updated = new UpdatedAccountDTO(id, account.getPassword(), account.getAccountStatus(), account.getBlockingReason());
        return new ResponseEntity<UpdatedAccountDTO>(updated, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/setup-password")
    public ResponseEntity<Void> setupPassword(@RequestBody SetupPasswordDTO dto) {
        //accountService.setupPassword(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordDTO dto) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProfileDTO> getProfile(@PathVariable Long id) {
        CreatedUserDTO createUserDTO = new CreatedUserDTO(1L, "Bojana", "Paunović", "adresa", "061234567", "image.png");

        GetProfileDTO profile = new GetProfileDTO(
                createUserDTO,
                null
        );

        return ResponseEntity.ok(profile);
    }

    @PostMapping("/approve-change/{id}")
    public ResponseEntity<String> approveChange(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reject-change/{id}")
    public ResponseEntity<String> rejectChange(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<GetProfileDTO> updateProfile(@PathVariable Long id, @RequestBody CreateUserDTO updatedUser) {
        CreatedUserDTO createUserDTO = new CreatedUserDTO(
                1L,
                updatedUser.getName(),
                updatedUser.getLastName(),
                updatedUser.getHomeAddress(),
                updatedUser.getPhone(),
                updatedUser.getImage()
        );

        GetProfileDTO updatedProfile = new GetProfileDTO(createUserDTO, null);

        return ResponseEntity.ok(updatedProfile);
    }

}