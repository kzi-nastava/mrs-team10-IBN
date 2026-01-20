package com.example.UberComp.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PanicSignal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Ride ride;
}
