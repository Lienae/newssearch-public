package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.AdminLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminLogRepository extends JpaRepository<AdminLog, Long> {
}
