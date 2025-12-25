package com.example.UberComp.model;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private AccountType accountType;

    @Column
    private AccountStatus accountStatus;

    @Column
    private String blockingReason;

    public Account(String email, String password, AccountType type){
        this.email = email;
        this.password = password;
        this.accountType = type;
        this.accountStatus = AccountStatus.UNVERIFIED;
        this.blockingReason = null;
    }
}
