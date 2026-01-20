package com.example.UberComp.enums;

import org.springframework.security.core.GrantedAuthority;

public enum AccountType implements GrantedAuthority {
    PASSENGER, DRIVER, ADMINISTRATOR;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    @Override
    public String getAuthority() {
        return toString();
    }
}
