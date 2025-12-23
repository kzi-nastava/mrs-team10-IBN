package com.example.UberComp.dto.account;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAccountDTO {
    private String email;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private String blockingReason;
}
