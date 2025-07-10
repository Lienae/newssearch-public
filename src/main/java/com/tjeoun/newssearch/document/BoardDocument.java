package com.tjeoun.newssearch.document;


import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.FieldType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.time.LocalDateTime;

@Data
@Document(indexName = "board")
public class BoardDocument {
  @Id
  private String id;

  @Field(type = FieldType.Text)  // 분석기 붙는 텍스트
  private String title;

  @Field(type = FieldType.Text)
  private String content;

  @Field(type = FieldType.Keyword)  // 분석기 없이 정렬/필터용
  private String writer;

  // String 타입으로 유지하되, JsonFormat 패턴은 출력될 문자열 형태에 맞춥니다.
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") // 밀리초 제거된 포맷
  @Field(type = FieldType.Keyword) // String으로 저장할 때 일반적으로 Keyword 또는 Text 사용
  private String createdDate;




}
