package com.example.UberComp.dto.account;

import com.example.UberComp.dto.user.CreateUserDTO;
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

    public RegisterDTO(AccountDTO accountDTO, CreateUserDTO createUserDTO) {
        this.email = accountDTO.getEmail();
        this.password = "null";
        this.type = AccountType.DRIVER;
        this.name = createUserDTO.getName();
        this.lastName = createUserDTO.getLastName();
        this.homeAddress = createUserDTO.getHomeAddress();
        this.phone = createUserDTO.getPhone();
        this.image = createUserDTO.getImage();
    }
}
