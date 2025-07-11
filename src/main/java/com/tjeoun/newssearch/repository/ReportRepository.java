package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.Report;
import com.tjeoun.newssearch.enums.ReportEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReportTypeAndTargetIdAndMember(ReportEnum reportType, Long targetId, Member member);
}
