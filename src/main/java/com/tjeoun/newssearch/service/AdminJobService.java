package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.AdminJobDto;
import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.repository.AdminJobRepository;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.NewsReplyRepository;
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
    private final NewsReplyRepository newsReplyRepository;
    private final BoardReplyRepository boardReplyRepository;
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
                    .map(adminJob -> AdminJobDto.fromEntity(adminJob, newsReplyRepository, boardReplyRepository));
            } else {
                boolean resolved = "RESOLVED".equalsIgnoreCase(filter);
                return adminJobRepository.findByIsResolvedAndRecordedTimeBetween(resolved, start, end, pageable)
                    .map(adminJob -> AdminJobDto.fromEntity(adminJob, newsReplyRepository, boardReplyRepository));
            }
        }

        // 날짜 검색 없으면 필터만 적용
        if ("RESOLVED".equalsIgnoreCase(filter)) {
            return adminJobRepository.findByIsResolved(true, pageable)
                .map(adminJob -> AdminJobDto.fromEntity(adminJob, newsReplyRepository, boardReplyRepository));
        } else if ("UNRESOLVED".equalsIgnoreCase(filter)) {
            return adminJobRepository.findByIsResolved(false, pageable)
                .map(adminJob -> AdminJobDto.fromEntity(adminJob, newsReplyRepository, boardReplyRepository));
        } else {
            return adminJobRepository.findAll(pageable)
                .map(adminJob -> AdminJobDto.fromEntity(adminJob, newsReplyRepository, boardReplyRepository));
        }
    }



    public void updateJobStatus(Long id, Boolean isResolved) {
        AdminJob job = adminJobRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("작업이 존재하지 않습니다."));
        job.setIsResolved(isResolved);
        adminJobRepository.save(job);
    }


    public void save(AdminJob adminJob) {
        adminJobRepository.save(adminJob);
    }

}