package com.example.UberComp.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
public class User {
    private Long id;
    private String name;
    private String lastName;
    private String homeAddress;
    private String phone;
    private String image;
    private List<Route> favoriteRoutes;
}
