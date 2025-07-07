package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.repository.PasswordTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenCleanupService {
    private final PasswordTokenRepository passwordTokenRepository;

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void cleanupExpiredResetPasswordToken() {
        passwordTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
