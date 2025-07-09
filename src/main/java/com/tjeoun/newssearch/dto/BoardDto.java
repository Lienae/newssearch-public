package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.NewsCategory;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardDto {
    private Long id;
    private String title;
    private String content;
    private Member author;
    private NewsCategory newsCategory;
    private Boolean isAdminArticle;

    private List<MultipartFile> files;


    public Board toEntity() {
        return Board.builder()
          .id(this.id)
          .title(this.title)
          .content(this.content)
          .author(this.author)
          .newsCategory(this.newsCategory != null ? this.newsCategory : NewsCategory.MISC)  // 기본값 처리
          .isAdminArticle(this.isAdminArticle != null ? this.isAdminArticle : false) // 기본값 false 처리
          .build();
    }
}
