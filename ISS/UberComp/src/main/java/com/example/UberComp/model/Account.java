package com.example.UberComp.model;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import lombok.Data;

@Data
public class Account {
    private Long id;
    private String email;
    private String password;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private String blockingReason;
}
