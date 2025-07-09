package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.entity.AdminLog;
import com.tjeoun.newssearch.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminLogService {
    private final AdminLogRepository adminLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(AdminLog adminLog) {
        adminLogRepository.save(adminLog);
    }
}
