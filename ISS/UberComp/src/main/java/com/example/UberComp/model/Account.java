package com.example.UberComp.model;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import lombok.Data;

@Data
public class Account {
    public Long id;
    public String email;
    public String password;
    public AccountType accountType;
    public AccountStatus accountStatus;
    public String blockingReason;
}
