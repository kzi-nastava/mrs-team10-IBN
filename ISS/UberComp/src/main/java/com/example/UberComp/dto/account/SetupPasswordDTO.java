package com.example.UberComp.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetupPasswordDTO {
    private String token;
    private String password;
}
