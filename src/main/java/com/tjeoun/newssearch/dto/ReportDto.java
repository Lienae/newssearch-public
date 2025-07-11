package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.ReportEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ReportDto {
    private Long id;
    private ReportEnum reportType;
    private LocalDateTime createdDate;
    private Long targetId;
    private Member member;
}
