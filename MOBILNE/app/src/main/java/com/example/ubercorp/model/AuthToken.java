package com.example.ubercorp.model;

public class AuthToken {
    private String accessToken;
    private Long expiresIn;
    public AuthToken() {}
    public AuthToken(String accessToken, Long expiresIn){
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }
}
