package com.example.UberComp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Entity(name = "app_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String homeAddress;

    @Column(nullable = false)
    private String phone;

    @Column
    private String image;

    @Column
    @OneToMany
    private List<Route> favoriteRoutes;

    @OneToOne
    private Account account;
}
