package com.example.UberComp.service;

import com.example.UberComp.dto.account.AccountDTO;
import com.example.UberComp.dto.account.GetAccountDTO;
import com.example.UberComp.dto.account.LogAccountDTO;
import com.example.UberComp.dto.account.RegisterDTO;
import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.dto.user.CreatedUserDTO;
import com.example.UberComp.dto.user.GetProfileDTO;
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

    public GetAccountDTO getbyId(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account != null)
            return new GetAccountDTO(account.getId(), account.getEmail(), account.getAccountType().toString());
        return null;
    }

    public GetProfileDTO getProfile(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        AccountDTO getAccount = new AccountDTO(account.getEmail());
        User user = account.getUser();
        CreatedUserDTO getUser = new CreatedUserDTO(user);
        return new GetProfileDTO(getUser, getAccount);
    }
    public GetProfileDTO updateProfile(Long accountId, CreateUserDTO updatedUser) throws Exception {
        Account account = accountRepository.findById(accountId).orElse(null);

        if (account == null) {
            throw new Exception("Account not found");
        }

        User user = account.getUser();
        if(user == null) {
            user = new User();
            account.setUser(user);
        }

        user.setName(updatedUser.getName());
        user.setLastName(updatedUser.getLastName());
        user.setHomeAddress(updatedUser.getHomeAddress());
        user.setPhone(updatedUser.getPhone());
        user.setImage(updatedUser.getImage());
        
        accountRepository.save(account);

        CreatedUserDTO createdUserDTO = new CreatedUserDTO(user);
        AccountDTO accountDTO = new AccountDTO(account.getEmail());

        userRepository.save(user);

        return new GetProfileDTO(createdUserDTO, accountDTO);
    }

}
