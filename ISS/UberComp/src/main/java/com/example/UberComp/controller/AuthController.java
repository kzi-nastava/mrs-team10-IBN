package com.example.UberComp.controller;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.account.RegisterDTO;
import com.example.UberComp.dto.account.SetPasswordDTO;
import com.example.UberComp.dto.auth.AuthTokenDTO;
import com.example.UberComp.dto.account.LogAccountDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.dto.user.GetProfileDTO;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.Account;
import com.example.UberComp.model.User;
import com.example.UberComp.service.AccountService;
import com.example.UberComp.service.DriverAvailabilityService;
import com.example.UberComp.utils.EmailUtils;
import com.example.UberComp.utils.TokenUtils;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DriverAvailabilityService driverAvailabilityService;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenDTO> createAuthenticationToken(
            @Valid @RequestBody LogAccountDTO authRequest, HttpServletResponse response) {
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getEmail(), authRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Account user = (Account) authentication.getPrincipal();
        if (user.getAccountType().equals(AccountType.DRIVER))
            driverAvailabilityService.setDriverStatus(user.getId(), true);
        String jwt = tokenUtils.generateToken(user);
        Long expiresIn = tokenUtils.getExpiredIn();

        return ResponseEntity.ok(new AuthTokenDTO(jwt, expiresIn));
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetProfileDTO> createAccount(@Valid @RequestBody RegisterDTO userData) throws Exception{
        User registered = accountService.register(userData);
        if (registered == null) return new ResponseEntity<>(HttpStatus.CONFLICT);
        AccountDTO registeredDTO = new AccountDTO(userData.getEmail());
        CreatedUserDTO createdUserDTO = new CreatedUserDTO(registered);
        GetProfileDTO profile = new GetProfileDTO(createdUserDTO, registeredDTO);
        return new ResponseEntity<GetProfileDTO>(profile, HttpStatus.CREATED);
    }

    @GetMapping(value = "/verify/{verifyID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> verifyAccount(@PathVariable String verifyID) {
        Account account = accountService.verify(verifyID);
        if(account == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if(account.getAccountStatus() == AccountStatus.BLOCKED){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping(value = "/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody AccountDTO account){
        if(accountService.generatePasswordResetToken(account.getEmail())){
            return ResponseEntity.ok(null);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/set-password/{token}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> checkSetPasswordToken(@PathVariable String token){
        if(accountService.checkSetPasswordToken(token)){
            return ResponseEntity.ok(null);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/set-password/{token}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> setPassword(@PathVariable String token, @Valid @RequestBody SetPasswordDTO password){
        if(accountService.setPassword(token, password)){
            return ResponseEntity.ok(null);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
