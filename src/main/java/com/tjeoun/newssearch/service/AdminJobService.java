package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.AdminJobDto;
import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.repository.AdminJobRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminJobService {
    private final AdminJobRepository adminJobRepository;

    public List<AdminJob> getJobs() {
        return adminJobRepository.findByIsResolvedFalseOrderByRecordedTimeDesc();
    }

    public Page<AdminJobDto> getFilteredJobs(String filter, LocalDate searchDate, Pageable pageable) {
        boolean filterAll = "ALL".equalsIgnoreCase(filter);

        if (searchDate != null) {
            LocalDateTime start = searchDate.atStartOfDay();
            LocalDateTime end = searchDate.atTime(LocalTime.MAX);

            if (filterAll) {
                return adminJobRepository.findByRecordedTimeBetween(start, end, pageable)
                    .map(AdminJobDto::fromEntity);
            } else {
                boolean resolved = "RESOLVED".equalsIgnoreCase(filter);
                return adminJobRepository.findByIsResolvedAndRecordedTimeBetween(resolved, start, end, pageable)
                    .map(AdminJobDto::fromEntity);
            }
        }

        // 날짜 검색 없으면 필터만 적용
        if ("RESOLVED".equalsIgnoreCase(filter)) {
            return adminJobRepository.findByIsResolved(true, pageable)
                .map(AdminJobDto::fromEntity);
        } else if ("UNRESOLVED".equalsIgnoreCase(filter)) {
            return adminJobRepository.findByIsResolved(false, pageable)
                .map(AdminJobDto::fromEntity);
        } else {
            return adminJobRepository.findAll(pageable)
                .map(AdminJobDto::fromEntity);
        }
    }



    public void updateJobStatus(Long id, Boolean isResolved) {
        AdminJob job = adminJobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("작업이 존재하지 않습니다."));
        job.setIsResolved(isResolved);
        adminJobRepository.save(job);
    }




}