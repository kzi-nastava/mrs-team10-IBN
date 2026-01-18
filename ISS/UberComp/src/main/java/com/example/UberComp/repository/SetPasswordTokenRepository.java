package com.example.UberComp.repository;

import com.example.UberComp.model.SetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SetPasswordTokenRepository extends JpaRepository<SetPasswordToken, Long> {
    SetPasswordToken getByToken(String token);
}
