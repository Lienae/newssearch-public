package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.AdminJobDto;
import com.tjeoun.newssearch.enums.AdminJobsEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AdminJobsEnum job;

    private Long targetId;
    private LocalDateTime recordedTime;
    private Boolean isResolved;

    public static AdminJob fromDto(AdminJobDto dto) {
        return AdminJob.builder()
                .job(dto.getJob())
                .targetId(dto.getTargetId())
                .recordedTime(LocalDateTime.now())
                .isResolved(false)
                .build();
    }
}
