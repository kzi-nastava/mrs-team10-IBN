package com.example.UberComp.repository;

import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmail(String email);
    Account findByVerification(String verification);
    Page<Account> findByAccountType(AccountType accountType, Pageable pageable);
}
