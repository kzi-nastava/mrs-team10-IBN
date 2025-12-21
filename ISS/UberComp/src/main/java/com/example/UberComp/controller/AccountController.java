package com.example.UberComp.controller;


import com.example.UberComp.dto.AccountDTO;
import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/api/account")
class AccountController {
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<AccountDTO>> getAccounts(){
        ArrayList<AccountDTO> accounts = new ArrayList<AccountDTO>();

        AccountDTO acc1 = new AccountDTO("user@gmail.com", "password", AccountType.PASSENGER, AccountStatus.UNVERIFIED, null);
        AccountDTO acc2 = new AccountDTO("niksa@gmail.com", "niksa", AccountType.PASSENGER, AccountStatus.VERIFIED, null);

        accounts.add(acc1);
        accounts.add(acc2);

        return new ResponseEntity<Collection<AccountDTO>>(accounts,HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDTO> getAccount(@PathVariable("id") Long id){
        AccountDTO acc = new AccountDTO("user@gmail.com", "password", AccountType.PASSENGER, AccountStatus.UNVERIFIED, null);
        return new ResponseEntity<AccountDTO>(acc, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO account) throws Exception{
        return new ResponseEntity<AccountDTO>(account, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDTO> updateAccount(@RequestBody AccountDTO account, @PathVariable("id") Long id) throws Exception{
        return new ResponseEntity<AccountDTO>(account, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteGreeting(@PathVariable("id") Long id) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
