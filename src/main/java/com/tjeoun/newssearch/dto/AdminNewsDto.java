package com.tjeoun.newssearch.dto;

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
public class AdminNewsDto {
  private Long id;
  private String url;
  private String title;
  private String imageUrl;
  private String content;
  private LocalDate publishDate;
  private String author;
  private NewsCategory category;
  private NewsMediaCompany mediaCompany;
  private Boolean isBlind;

  public static AdminNewsDto fromEntity(News news) {
    return AdminNewsDto.builder()
      .id(news.getId())
      .url(news.getUrl())
      .title(news.getTitle())
      .imageUrl(news.getImageUrl())
      .content(news.getContent())
      .publishDate(news.getPublishDate())
      .author(news.getAuthor())
      .category(news.getCategory())
      .mediaCompany(news.getMediaCompany())
      .isBlind(news.isBlind())
      .build();
  }

  public static AdminNewsDto fromNewsDto(NewsDto newsDto) {
    return AdminNewsDto.builder()
        .id(newsDto.getId())
        .url(newsDto.getUrl())
        .title(newsDto.getTitle())
        .imageUrl(newsDto.getImageUrl())
        .content(newsDto.getContent())
        .publishDate(newsDto.getPublishDate())
        .author(newsDto.getAuthor())
        .category(newsDto.getCategory())
        .mediaCompany(newsDto.getMediaCompany())
        .isBlind(newsDto.getIs_blind())
        .build();
  }

}
