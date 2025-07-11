package com.tjeoun.newssearch.document;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.tjeoun.newssearch.enums.NewsCategory;
import org.springframework.data.elasticsearch.annotations.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
@Document(indexName = "board")
public class BoardDocument {
  @Id
  private String id;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer", searchAnalyzer = "korean_analyzer")
  private String title;

  @Field(type = FieldType.Text, analyzer = "korean_analyzer", searchAnalyzer = "korean_analyzer")
  private String content;

  @Field(type = FieldType.Keyword)
  private String writer;

  // createdDate는 여전히 keyword 타입으로 유지하며, 저장 시 String으로 포맷팅
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  @Field(type = FieldType.Keyword)
  private String createdDate;

  @Field(type = FieldType.Keyword)
  private NewsCategory newsCategory;

  @Field(type = FieldType.Boolean)
  private boolean isAdminArticle;

  @Field(type = FieldType.Boolean)
  private boolean isBlind;
}
