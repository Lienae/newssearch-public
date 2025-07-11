package com.tjeoun.newssearch.document;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;

@Document(indexName = "news_articles")
@Getter
@Setter
public class NewsDocument {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String url;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "nori"),
            otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword)})
    private String title;

    @Field(type = FieldType.Text)
    private String imageUrl;

    @MultiField(mainField = @Field(type = FieldType.Text, analyzer = "nori"),
            otherFields = {@InnerField(suffix = "keyword", type = FieldType.Keyword)})
    private String content;

    @Field(type = FieldType.Date, pattern = "yyyy-MM-dd")
    private LocalDate publishDate;

    @Field(type = FieldType.Keyword)
    private String author;

    @Field(type = FieldType.Keyword)
    private String category;

    @Field(type = FieldType.Keyword)
    private String mediaCompany;
}
