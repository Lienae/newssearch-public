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
    public void save(Long targetId, String email, ReportEnum reportEnum) throws DataIntegrityViolationException {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("user not found"));
        if(!reportRepository.existsByReportTypeAndTargetIdAndMember(reportEnum, targetId, member)) {
            AdminJob adminJob =  AdminJob.fromDto(AdminJobDto.builder()
                    .targetId(targetId)
                    .build());
            Report report = Report.fromDto(ReportDto.builder()
                    .member(member)
                    .reportType(reportEnum)
                    .targetId(targetId)
                    .build());
            switch (reportEnum) {
                case NEWS_REPLY -> adminJob.setJob(AdminJobsEnum.NEWS_REPLY_REPORT);
                case BOARD -> adminJob.setJob(AdminJobsEnum.BOARD_REPORT);
                case BOARD_REPLY -> adminJob.setJob(AdminJobsEnum.BOARD_REPLY_REPORT);
            }
            adminJobRepository.save(adminJob);
            reportRepository.save(report);
        }
    }
}
