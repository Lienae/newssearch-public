package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.enums.NewsCategory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "longtext")
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    @Column(nullable = false)
    private String password;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NewsCategory newsCategory;

    @Column(nullable = false)
    private boolean is_blind;

    @Column(nullable = false)
    private boolean is_admin_article;

    public static Board createBoard(BoardDto dto, PasswordEncoder passwordEncoder) {
        return Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(dto.getAuthor())
                .password(passwordEncoder.encode(dto.getPassword()))
                .newsCategory(dto.getNewsCategory())
                .is_admin_article(Boolean.TRUE.equals(dto.getIsAdminArticle()))
                .build();
    }
}
