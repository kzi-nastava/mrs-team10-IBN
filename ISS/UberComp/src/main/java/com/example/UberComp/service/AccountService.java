package com.example.UberComp.service;

import com.example.UberComp.dto.account.LogAccountDTO;
import com.example.UberComp.dto.account.CreateAccountDTO;
import com.example.UberComp.model.Account;
import com.example.UberComp.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account register(CreateAccountDTO account){
        if (accountRepository.findOneByEmail(account.getEmail()) != null){
            Account newAccount = new Account(account.getEmail(),account.getPassword(), account.getType());
            return accountRepository.save(newAccount);
        } else {
            return null;
        }
    }

    public Account login(LogAccountDTO accountDTO) {
        Account account = accountRepository.findOneByEmail(accountDTO.getEmail());
        if (account.getPassword().equals(accountDTO.getPassword())){
            return account;
        } else {
            return null;
        }
    }
}
