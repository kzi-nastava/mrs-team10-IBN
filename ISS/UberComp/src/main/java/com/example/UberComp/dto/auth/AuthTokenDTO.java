package com.example.UberComp.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthTokenDTO {
    private String accessToken;
    private Long expiresIn;
}
