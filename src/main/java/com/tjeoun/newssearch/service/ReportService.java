package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.AdminJobDto;
import com.tjeoun.newssearch.dto.ReportDto;
import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.Report;
import com.tjeoun.newssearch.enums.AdminJobsEnum;
import com.tjeoun.newssearch.enums.ReportEnum;
import com.tjeoun.newssearch.repository.AdminJobRepository;
import com.tjeoun.newssearch.repository.MemberRepository;
import com.tjeoun.newssearch.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final AdminJobRepository adminJobRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void save(Long targetId, Long userId, ReportEnum reportEnum) throws DataIntegrityViolationException {
        Member member = memberRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        if(!reportRepository.existsByReportTypeAndTargetIdAndMember(reportEnum, targetId, member)) {
            AdminJob adminJob = null;
            Report report = null;
            switch (reportEnum) {
                case NEWS_REPLY -> {
                    System.out.println("news job call");
                    throw new RuntimeException("Not implemented yet");
                }
                case BOARD -> {
                    adminJob = AdminJob.fromDto(AdminJobDto.builder()
                            .job(AdminJobsEnum.BOARD_REPORT)
                            .targetId(targetId)
                            .build());
                    report = Report.fromDto(ReportDto.builder()
                            .member(memberRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("user not found")))
                            .reportType(reportEnum)
                            .targetId(targetId)
                            .build());
                }
                case BOARD_REPLY -> {
                    adminJob = AdminJob.fromDto(AdminJobDto.builder()
                            .job(AdminJobsEnum.BOARD_REPLY_REPORT)
                            .targetId(targetId)
                            .build());
                    report = Report.fromDto(ReportDto.builder()
                            .member(memberRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("user not found")))
                            .reportType(reportEnum)
                            .targetId(targetId)
                            .build());
                }
            }
            adminJobRepository.save(adminJob);
            reportRepository.save(report);
        }
    }
}
