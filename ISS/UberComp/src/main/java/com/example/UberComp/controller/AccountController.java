package com.example.UberComp.controller;


import com.example.UberComp.dto.account.CreateAccountDTO;
import com.example.UberComp.dto.account.GetAccountDTO;
import com.example.UberComp.dto.account.UpdateAccountDTO;
import com.example.UberComp.dto.account.UpdatedAccountDTO;
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
    public ResponseEntity<Collection<GetAccountDTO>> getAccounts(){
        ArrayList<GetAccountDTO> accounts = new ArrayList<GetAccountDTO>();

        GetAccountDTO acc1 = new GetAccountDTO("user@gmail.com", AccountType.PASSENGER, AccountStatus.UNVERIFIED, null);
        GetAccountDTO acc2 = new GetAccountDTO("niksa@gmail.com", AccountType.PASSENGER, AccountStatus.VERIFIED, null);

        accounts.add(acc1);
        accounts.add(acc2);

        return new ResponseEntity<Collection<GetAccountDTO>>(accounts,HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAccountDTO> getAccount(@PathVariable("id") Long id){
        GetAccountDTO acc = new GetAccountDTO("user@gmail.com", AccountType.PASSENGER, AccountStatus.UNVERIFIED, null);
        return new ResponseEntity<GetAccountDTO>(acc, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateAccountDTO> createAccount(@RequestBody CreateAccountDTO account) throws Exception{
        return new ResponseEntity<CreateAccountDTO>(account, HttpStatus.CREATED);
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
}
