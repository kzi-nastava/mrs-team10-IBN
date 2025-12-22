package com.example.UberComp.dto;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAccountDTO {
    private String email;
    private String password;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private String blockingReason;
}
