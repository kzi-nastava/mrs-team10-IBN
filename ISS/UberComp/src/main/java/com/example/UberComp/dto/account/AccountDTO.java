package com.example.UberComp.dto.account;

import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private String email;

    public AccountDTO(Account account) {
        this.email = account.getEmail();
    }
}