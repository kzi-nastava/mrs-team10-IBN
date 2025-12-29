package com.example.UberComp.dto.account;

import com.example.UberComp.enums.AccountType;
import lombok.Data;

@Data
public class CreateAccountDTO {
    private String email;
    private String password;
    private AccountType type;
}
