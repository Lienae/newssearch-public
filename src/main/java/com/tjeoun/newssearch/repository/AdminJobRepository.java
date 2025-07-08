package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.AdminJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminJobRepository extends JpaRepository<AdminJob, Long> {
    List<AdminJob> findByIsResolvedFalseOrderByRecordedTimeDesc();
}
