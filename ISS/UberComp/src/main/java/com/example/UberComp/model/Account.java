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

    @Column(name = "account_type", columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "account_status", columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Column
    private String blockingReason;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    public Account(String email, String password, AccountType type){
        this.email = email;
        this.password = password;
        this.accountType = type;
        this.accountStatus = AccountStatus.UNVERIFIED;
        this.blockingReason = null;
    }
}
