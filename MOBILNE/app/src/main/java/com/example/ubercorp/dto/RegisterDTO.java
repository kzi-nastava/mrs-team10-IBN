package com.example.ubercorp.dto;

import com.example.ubercorp.enums.AccountType;

public class RegisterDTO {
    public RegisterDTO(String email, String password, AccountType type, String name, String lastName, String homeAddress, String phone, String image) {
        this.email = email;
        this.password = password;
        this.type = type;
        this.name = name;
        this.lastName = lastName;
        this.homeAddress = homeAddress;
        this.phone = phone;
        this.image = image;
    }

    private String email;
    private String password;
    private AccountType type;
    private String name;
    private String lastName;
    private String homeAddress;
    private String phone;
    private String image;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
