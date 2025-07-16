package com.tjeoun.newssearch.document;

import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.enums.NewsCategory;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "board")
@Setting(settingPath = "elasticsearch/settings/nori-settings.json")
public class BoardDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, analyzer = "nori", fielddata = true)
    private String title;

    @Field(type = FieldType.Text, analyzer = "nori", fielddata = true)
    private String content;

    @Field(type = FieldType.Keyword)
    private String email;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private OffsetDateTime createdDate;

    @Field(type = FieldType.Keyword)
    private String newsCategory;

    @Field(type = FieldType.Boolean)
    private boolean isAdminArticle;

    @Field(type = FieldType.Boolean)
    private boolean isBlind;

    public static BoardDocument fromEntity(Board board) {
        return BoardDocument.builder()
            .id(String.valueOf(board.getId())) // Elasticsearch는 ID를 문자열로 저장
            .title(board.getTitle())
            .content(board.getContent())
            .email(board.getAuthor().getEmail()) // 작성자(Member)의 이메일
            .createdDate(board.getCreatedDate().atOffset(ZoneOffset.UTC)) // LocalDateTime → OffsetDateTime
            .newsCategory(board.getNewsCategory().name()) // Enum → String
            .isAdminArticle(board.getNews() == null) // 뉴스 연관 엔티티가 없으면 관리자가 작성한 게시글로 판단
            .isBlind(Boolean.TRUE.equals(board.getIsBlind())) // null-safe 처리
            .build();
    }


}
