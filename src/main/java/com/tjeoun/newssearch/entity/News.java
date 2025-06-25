package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String content;

    @Column
    private LocalDateTime publishDate;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NewsCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NewsMediaCompany mediaCompany;

    public static NewsDocument toDocument(News news) {
        NewsDocument doc = new NewsDocument();
        doc.setId(news.getId().toString());
        doc.setUrl(news.getUrl());
        doc.setTitle(news.getTitle());
        doc.setImageUrl(news.getImageUrl());
        doc.setContent(news.getContent());
        doc.setPublishDate(news.getPublishDate());
        doc.setAuthor(news.getAuthor());
        doc.setCategory(news.getCategory().name());
        doc.setMediaCompany(news.getMediaCompany().name());
        return doc;
    }
    public static News createNewsFromDto(NewsDto dto) {
        return News.builder()
                .id(dto.getId())
                .url(dto.getUrl())
                .title(dto.getTitle())
                .imageUrl(dto.getImageUrl())
                .content(dto.getContent())
                .publishDate(dto.getPublishDate())
                .author(dto.getAuthor())
                .category(dto.getCategory())
                .mediaCompany(dto.getMediaCompany())
                .build();
    }
}
