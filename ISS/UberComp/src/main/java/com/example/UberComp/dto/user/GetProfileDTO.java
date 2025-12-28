package com.example.UberComp.dto.user;

import com.example.UberComp.dto.account.AccountDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetProfileDTO {
    private CreatedUserDTO createdUserDTO;
    private AccountDTO accountDTO;
}
