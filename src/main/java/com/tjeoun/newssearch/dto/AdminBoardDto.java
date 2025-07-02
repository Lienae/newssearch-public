package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.NewsCategory;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AdminBoardDto {
  private Long id;
  private String title;
  private String content;
  private Long authorId;
  private String authorName;
  private String password;
  private NewsCategory newsCategory;
  private String newsTitle;
  private Boolean isAdminArticle;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
  // private Boolean isAdmin;
}
