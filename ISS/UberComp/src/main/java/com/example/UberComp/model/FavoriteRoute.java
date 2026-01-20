package com.example.UberComp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "favorite_routes", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "route_id"}))
public class FavoriteRoute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;
}