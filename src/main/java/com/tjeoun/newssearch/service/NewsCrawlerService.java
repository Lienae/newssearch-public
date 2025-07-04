package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.helper.*;
import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.*;


import static com.tjeoun.newssearch.util.NewsCrawlUtils.*;

@Service
@RequiredArgsConstructor
public class NewsCrawlerService {
    private final PlatformTransactionManager transactionManager;
    private final NewsRepository newsRepository;
    private final NewsDocumentRepository newsDocumentRepository;

    public void getHaniArticles() {
        List<Map<String, String>> articles = HaniCrawlerHelper.getArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }

    @Transactional
    public void getJoongangArticles() {
        List<Map<String, String>> articles = JoongangCrawlerHelper.getJoongangArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }

    @Transactional
    public void getKhanArticles() {
        // 카테고리당 최대 30개 수집
        List<Map<String, String>> articles = KhanCrawlerHelper.collectAllArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }

    @Transactional
    public void getYtnArticles() {
        List<Map<String, String>> articles = YtnNewsCrawlerHelper.collectArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }

    @Transactional
    public void getDongaArticles() {
        List<Map<String, String>> articles = DongaCrawlerHelper.collectArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }
}
