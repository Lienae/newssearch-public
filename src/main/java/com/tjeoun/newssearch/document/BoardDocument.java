package com.tjeoun.newssearch.document;

import com.tjeoun.newssearch.enums.NewsCategory;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.OffsetDateTime;

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
    private NewsCategory newsCategory;

    @Field(type = FieldType.Boolean)
    private boolean isAdminArticle;

    @Field(type = FieldType.Boolean)
    private boolean isBlind;
}
