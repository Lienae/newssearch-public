package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
