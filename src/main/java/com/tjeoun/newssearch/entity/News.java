package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private String title;

    @Column
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "longtext")
    private String content;

    @Column(nullable = false)
    private LocalDate publishDate;

    @Column
    private String author;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NewsCategory category;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NewsMediaCompany mediaCompany;

    public static NewsDocument toDocument(News news) {
        NewsDocument doc = new NewsDocument();
        doc.setId(news.getId().toString());
        doc.setUrl(news.getUrl());
        doc.setTitle(news.getTitle());
        doc.setImageUrl(news.getImageUrl());
        doc.setContent(news.getContent());
        doc.setPublishDate(news.getPublishDate());
        doc.setAuthor(news.getAuthor());
        doc.setCategory(news.getCategory().name());
        doc.setMediaCompany(news.getMediaCompany().name());
        return doc;
    }
    public static News createNewsFromDto(NewsDto dto) {
        return News.builder()
                .id(dto.getId())
                .url(dto.getUrl())
                .title(dto.getTitle())
                .imageUrl(dto.getImageUrl())
                .content(dto.getContent())
                .publishDate(dto.getPublishDate())
                .author(dto.getAuthor())
                .category(dto.getCategory())
                .mediaCompany(dto.getMediaCompany())
                .build();
    }
    public static News createNewsFromMap(Map<String, String> article) {
        return News.builder()
                .url(article.get("링크"))
                .title(article.get("제목"))
                .imageUrl(article.get("대표이미지"))
                .content(article.get("내용"))
                // 날짜 정보만 사용할 것이므로 시간정보는 00:00:00으로 통일하여 기록
                .publishDate(
                        LocalDate.parse(
                                article.get("날짜"),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        ))
                .author(article.get("기자명"))
                .category(switch(article.get("카테고리")) {
                    case "정치" -> NewsCategory.POLITICS;
                    case "경제" -> NewsCategory.ECONOMY;
                    case "사회" -> NewsCategory.SOCIAL;
                    case "문화" -> NewsCategory.CULTURE;
                    case "스포츠" -> NewsCategory.SPORTS;
                    default -> NewsCategory.MISC;
                })
                .mediaCompany(switch(article.get("언론사")) {
                    case "한겨레" -> NewsMediaCompany.HANI;
                    case "중앙일보" -> NewsMediaCompany.JOONGANG;
                    case "동아일보" -> NewsMediaCompany.DONGA;
                    case "YTN" -> NewsMediaCompany.YTN;
                    default -> NewsMediaCompany.KHAN;
                })
                .build();

    }

}
