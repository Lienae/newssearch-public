package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AdminNewsDto {
  private Long id;
  private String url;
  private String title;
  private String imageUrl;
  private String content;
  private LocalDateTime publishDate;
  private String author;
  private NewsCategory category;
  private NewsMediaCompany mediaCompany;

  public static AdminNewsDto convertToDto(News news) {
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
      .build();
  }
}
