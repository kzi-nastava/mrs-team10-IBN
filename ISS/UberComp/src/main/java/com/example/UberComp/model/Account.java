package com.example.UberComp.model;

import com.example.UberComp.enums.AccountStatus;
import com.example.UberComp.enums.AccountType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Account implements UserDetails {
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

    @Column(name = "last_pass_reset")
    private Timestamp lastPasswordResetDate;

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
        this.lastPasswordResetDate = new Timestamp(new Date().getTime());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(this.accountType);
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
