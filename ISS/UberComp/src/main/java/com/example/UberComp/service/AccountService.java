package com.example.UberComp.service;

import com.example.UberComp.dto.account.LogAccountDTO;
import com.example.UberComp.dto.account.RegisterDTO;
import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.model.Account;
import com.example.UberComp.model.User;
import com.example.UberComp.repository.AccountRepository;
import com.example.UberComp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private UserRepository userRepository;

    public User register(RegisterDTO account){
        if (accountRepository.findByEmail(account.getEmail()) == null){
            Account newAccount = new Account(account.getEmail(),account.getPassword(), account.getType());
            User newUser = new User();
            newUser.setName(account.getName());
            newUser.setLastName(account.getLastName());
            newUser.setHomeAddress(account.getHomeAddress());
            newUser.setPhone(account.getPhone());
            newUser.setImage(account.getImage());
            newUser.setAccount(newAccount);
            newAccount.setUser(newUser);
            accountRepository.save(newAccount);
            return userRepository.save(newUser);
        } else {
            return null;
        }
    }

    public Account login(LogAccountDTO accountDTO) {
        Account account = accountRepository.findByEmail(accountDTO.getEmail());
        if (account != null && account.getPassword().equals(accountDTO.getPassword())){
            return account;
        } else {
            return null;
        }
    }
}
