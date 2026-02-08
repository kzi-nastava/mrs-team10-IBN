package com.example.UberComp.dto.account;

import com.example.UberComp.dto.user.CreateUserDTO;
import com.example.UberComp.enums.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    @Email(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;
    @Size(min = 6)
    private String password;
    @NotNull
    private AccountType type;
    @NotBlank
    private String name;
    @NotBlank
    private String lastName;
    @NotBlank
    private String homeAddress;
    @NotBlank
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
