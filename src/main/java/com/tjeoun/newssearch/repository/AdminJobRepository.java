package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.AdminJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminJobRepository extends JpaRepository<AdminJob, Long> {
    List<AdminJob> findByIsResolvedFalseOrderByRecordedTimeDesc();
    Page<AdminJob> findByIsResolved(boolean isResolved, Pageable pageable);
    Page<AdminJob> findByRecordedTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<AdminJob> findByIsResolvedAndRecordedTimeBetween(boolean isResolved, LocalDateTime start, LocalDateTime end, Pageable pageable);



}
