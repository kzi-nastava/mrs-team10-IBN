package com.example.ubercorp.dto;

public class CreateUserDTO {
    private String name;
    private String lastName;
    private String homeAddress;
    private String phone;
    private String image;

    public CreateUserDTO(String firstName, String lastName, String address, String phone, String image) {
        this.name = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.homeAddress = address;
        this.image = image;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
