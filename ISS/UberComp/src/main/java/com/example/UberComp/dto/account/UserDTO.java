package com.example.UberComp.dto.account;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String name;
    private String lastname;
    private String email;
    private String phoneNumber;
    private AccountStatus accountStatus;
    private String blockingReason;

    public UserDTO(User user) {
        this.name = user.getName();
        this.lastname = user.getLastName();
        this.phoneNumber = user.getPhone();
    }
}