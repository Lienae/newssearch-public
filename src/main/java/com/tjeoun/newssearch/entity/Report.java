package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.ReportDto;
import com.tjeoun.newssearch.enums.ReportEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "report_type", "target_id"}))
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private ReportEnum reportType;

    @CreatedDate
    private LocalDateTime createdDate;
    private Long targetId;

    public static Report fromDto(ReportDto dto) {
        return Report.builder()
                .member(dto.getMember())
                .reportType(dto.getReportType())
                .createdDate(LocalDateTime.now())
                .targetId(dto.getTargetId())
                .build();
    }
}
