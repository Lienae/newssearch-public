package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.NewsReplyDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NewsReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    public static NewsReply createNewsReply(NewsReplyDto dto, PasswordEncoder passwordEncoder) {
        return NewsReply.builder()
                .content(dto.getContent())
                .news(dto.getNews())
                .member(dto.getMember())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
    }
}
