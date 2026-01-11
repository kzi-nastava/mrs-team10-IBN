package com.example.UberComp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "app_user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
@DiscriminatorValue("User")
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

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column
    @OneToMany
    private List<Route> favoriteRoutes;

    @OneToOne
    private Account account;
}
