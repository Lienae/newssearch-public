package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class NewsDto {
    private Long id;
    private String url;
    private String title;
    private String imageUrl;
    private String content;
    private LocalDate publishDate;
    private String author;
    private NewsCategory category;
    private NewsMediaCompany mediaCompany;
    private Boolean is_blind;

    public static NewsDto fromEntity(News news) {
        return NewsDto.builder()
                .id(news.getId())
                .url(news.getUrl())
                .title(news.getTitle())
                .imageUrl(news.getImageUrl())
                .content(news.getContent())
                .publishDate(news.getPublishDate())
                .author(news.getAuthor())
                .category(news.getCategory())
                .mediaCompany(news.getMediaCompany())
                .is_blind(news.isBlind())
                .build();
    }

    public static NewsDto fromDocument(NewsDocument doc) {
        return NewsDto.builder()
            .id(Long.parseLong(doc.getId()))
            .url(doc.getUrl())
            .title(doc.getTitle())
            .imageUrl(doc.getImageUrl())
            .content(doc.getContent())
            .publishDate(doc.getPublishDate())
            .author(doc.getAuthor())
            .mediaCompany(NewsMediaCompany.valueOf(doc.getMediaCompany())) // 대소문자 구분.. 대문자로 변환해야하나?
            .category(NewsCategory.valueOf(doc.getCategory()))
            .build();
    }
}

