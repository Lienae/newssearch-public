package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordTokenRepository extends JpaRepository<PasswordToken, Long> {
    @Modifying
    void deleteByExpiryDateBefore(LocalDateTime expiryDate);
    Optional<PasswordToken> findByToken(String token);
    @Modifying
    void deleteByToken(String token);
}
