package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.AttachFileDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AttachFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name="board_id")
    private Board board;

    public void setBoard(Board board) {
        this.board = board;
    }

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String serverFilename;

    public static AttachFile createAttachFile(AttachFileDto dto) {
        return AttachFile.builder()
                .board(dto.getBoard())
                .size(dto.getFileSize())
                .originalFilename(dto.getFileName())
                .serverFilename(UUID.randomUUID().toString())
                .build();
    }
}
