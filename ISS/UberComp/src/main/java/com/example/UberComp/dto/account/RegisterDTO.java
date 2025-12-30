package com.example.UberComp.dto.account;

import com.example.UberComp.enums.AccountType;
import lombok.Data;

@Data
public class RegisterDTO {
    private String email;
    private String password;
    private AccountType type;
    private String name;
    private String lastName;
    private String homeAddress;
    private String phone;
    private String image;
}
