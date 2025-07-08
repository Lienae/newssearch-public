package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.enums.AdminJobsEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AdminJobDto {

    private Long id;
    private AdminJobsEnum job;
    private String url;
    private LocalDateTime recordedTime;
    private Boolean isResolved;

    public static AdminJobDto fromEntity(AdminJob job) {
        return AdminJobDto.builder()
                .id(job.getId())
                .job(job.getJob())
                .url(job.getUrl())
                .recordedTime(job.getRecordedTime())
                .isResolved(job.getIsResolved())
                .build();
    }

}
