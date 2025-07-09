package com.tjeoun.newssearch.entity;

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

    private String url;
    private LocalDateTime recordedTime;
    private Boolean isResolved;
}
