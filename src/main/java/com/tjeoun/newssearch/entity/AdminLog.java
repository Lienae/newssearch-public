package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.AdminLogDTO;
import com.tjeoun.newssearch.enums.AdminLogEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AdminLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private AdminLogEnum action;

    private Long targetId;
    private String targetTable;

    @Column(columnDefinition = "char(17)", nullable = false)
    private String ipAddress;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime auditTime;

    public static AdminLog createEntity(AdminLogDTO dto) {
        return AdminLog.builder()
                .member(dto.getAdmin())
                .action(dto.getAction())
                .targetId(dto.getTargetId())
                .targetTable(dto.getTargetTable())
                .ipAddress(dto.getIpAddress())
                .auditTime(LocalDateTime.now())
                .build();
    }
}
