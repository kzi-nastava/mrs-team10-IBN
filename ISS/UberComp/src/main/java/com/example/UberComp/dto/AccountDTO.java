package com.example.UberComp.dto;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDTO {
    public String email;
    public String password;
    public AccountType accountType;
    public AccountStatus accountStatus;
    public String blockingReason;
}
