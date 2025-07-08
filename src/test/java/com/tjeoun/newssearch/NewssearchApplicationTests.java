package com.tjeoun.newssearch;

import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.service.NewsCrawlerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
@SpringBootTest
class NewssearchApplicationTests {

    @Autowired
    private NewsCrawlerService newsCrawlerService;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsDocumentRepository newsDocumentRepository;

    @Test
    void contextLoads() {
//        assertThatCode(() -> {
//            newsCrawlerService.getHaniArticles();
//            newsCrawlerService.getKhanArticles();
//            newsCrawlerService.getJoongangArticles();
//        }).doesNotThrowAnyException();
    }

}
