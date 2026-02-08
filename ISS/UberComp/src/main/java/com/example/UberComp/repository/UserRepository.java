package com.example.UberComp.repository;

import com.example.UberComp.enums.AccountType;
import com.example.UberComp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
        SELECT user FROM com.example.UberComp.model.User user
        JOIN user.account acc
        WHERE acc.accountType = :type
        """)
    List<User> findAllByRole(AccountType type);
    Optional<User> findByAccountEmail(String email);
}
