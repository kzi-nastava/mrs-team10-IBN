package com.example.ubercorp.model;

public class Credentials {
    private String email;
    private String password;

    public Credentials() {}
    public Credentials(String email, String password){
        this.email = email;
        this.password = password;
    }
    public String getEmail() {
        return this.email;
    }
    public String getPassword() {
        return password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
