package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.AdminJobDto;
import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.repository.AdminJobRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminJobService {
    private final AdminJobRepository adminJobRepository;

    public List<AdminJob> getJobs() {
        return adminJobRepository.findByIsResolvedFalseOrderByRecordedTimeDesc();
    }

    public List<AdminJobDto> getAllJobsAsDto() {
        return adminJobRepository.findAll(Sort.by(Sort.Direction.DESC, "recordedTime"))
                .stream()
                .map(AdminJobDto::fromEntity)
                .toList();
    }

    public void updateJobStatus(Long id, Boolean isResolved) {
        AdminJob job = adminJobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("작업이 존재하지 않습니다."));
        job.setIsResolved(isResolved);
        adminJobRepository.save(job);
    }

}