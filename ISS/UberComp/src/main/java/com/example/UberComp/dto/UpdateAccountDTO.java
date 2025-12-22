package com.example.UberComp.dto;

import com.example.UberComp.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateAccountDTO {
    private String password;
    private AccountStatus accountStatus;
    private String blockingReason;
}
