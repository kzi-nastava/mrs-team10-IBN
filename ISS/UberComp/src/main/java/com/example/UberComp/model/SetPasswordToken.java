package com.example.UberComp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Data
@Entity(name = "set_password_token")
@NoArgsConstructor
public class SetPasswordToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String token;

    @Column
    private Timestamp createdAt;

    @OneToOne
    private Account account;

    public SetPasswordToken(Account account){
        this.token = UUID.randomUUID().toString();
        this.createdAt = new Timestamp(new Date().getTime());
        this.account = account;
    }
}
