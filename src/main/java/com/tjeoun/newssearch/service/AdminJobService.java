package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.repository.AdminJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminJobService {
    private final AdminJobRepository adminJobRepository;

    public List<AdminJob> getJobs() {
        return adminJobRepository.findByIsResolvedFalseOrderByRecordedTimeDesc();
    }
}