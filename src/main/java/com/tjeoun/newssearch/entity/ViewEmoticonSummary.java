package com.tjeoun.newssearch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.ToString;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "view_emoticon_summary")
@ToString
public class ViewEmoticonSummary {
    @Id
    private Long news_id;
    private Integer like_count;
    private Integer dislike_count;
    private Integer funny_count;
    private Integer sad_count;
    private Integer angry_count;
}
