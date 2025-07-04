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
import java.util.ArrayList;
import java.util.List;

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
    private Boolean isBlind;

    @Column(nullable = false)
    private boolean isAdminArticle;



    public static Board createBoard(BoardDto dto) {


        System.out.println("DEBUG: author = " + dto.getAuthor());
        System.out.println("DEBUG: author pw = " + dto.getAuthor().getPassword());
        return Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(dto.getAuthor())
                .password(dto.getPassword() != null ? dto.getPassword() : "mockpw")
                .newsCategory(dto.getNewsCategory())
                .isAdminArticle(Boolean.TRUE.equals(dto.getIsAdminArticle()))
                .isBlind(false)
                .build();
    }
}
