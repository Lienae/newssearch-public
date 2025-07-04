package com.tjeoun.newssearch.dto;

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
}
