package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Board;
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
  private NewsCategory newsCategory;
  private String newsTitle;
  private Boolean isAdminArticle;
  private LocalDateTime createdDate;
  private LocalDateTime modifiedDate;
  private Boolean is_blind; // boolean 타입은 null 값을 허용하지 않음
  // private Boolean isAdmin;

  public static AdminBoardDto convertToDto(Board board) {
    return AdminBoardDto.builder()
      .id(board.getId())
      .title(board.getTitle())
      .content(board.getContent())
      .authorId(board.getAuthor().getId())
      .authorName(board.getAuthor().getName())
      .newsCategory(board.getNewsCategory())
      .newsTitle(board.getNews() != null ? board.getNews().getTitle() : null) // 현재 데이터에 News가 없는 경우
      .createdDate(board.getCreatedDate())
      .modifiedDate(board.getModifiedDate())
      .is_blind(board.is_blind())
      .build();
  }


}
