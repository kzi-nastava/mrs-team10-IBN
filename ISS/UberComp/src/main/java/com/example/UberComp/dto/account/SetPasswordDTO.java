package com.example.UberComp.dto.account;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetPasswordDTO {

    @Size(min = 6)
    private String password;
}
