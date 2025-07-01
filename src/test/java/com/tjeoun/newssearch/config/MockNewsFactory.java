package com.tjeoun.newssearch.config;

import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MockNewsFactory {


    @Autowired
    NewsRepository newsRepository;

    public News createNews() {
        return newsRepository.save(
                News.builder()
                        .publishDate(LocalDateTime.now())
                        .author("test")
                        .content("text")
                        .imageUrl("test/url/file.jpg")
                        .title("title")
                        .url("http://test.url")
                        .category(NewsCategory.SOCIAL)
                        .mediaCompany(NewsMediaCompany.DONGA)
                        .build()
        );
    }


}
