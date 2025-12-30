package com.example.UberComp.enums;

public enum AccountType {
    PASSENGER, DRIVER, ADMINISTRATOR;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
