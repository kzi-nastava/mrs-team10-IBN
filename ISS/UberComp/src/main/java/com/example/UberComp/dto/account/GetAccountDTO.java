package com.example.UberComp.dto.account;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAccountDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String phoneNumber;
}
