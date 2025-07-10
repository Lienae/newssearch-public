package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.helper.*;
import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.*;

import static com.tjeoun.newssearch.util.NewsCrawlUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsCrawlerService {

    private final PlatformTransactionManager transactionManager;
    private final NewsRepository newsRepository;
    private final NewsDocumentRepository newsDocumentRepository;

    @Scheduled(cron = "0 0 0/6 * * *") // 6시간마다 실행
    public void getHaniArticles() {
        log.info("한겨레 기사 수집 시작");
        List<Map<String, String>> articles = HaniCrawlerHelper.getArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }

    @Scheduled(cron = "0 10 0/6 * * *")
    public void getJoongangArticles() {
        log.info("중앙일보 기사 수집 시작");
        List<Map<String, String>> articles = JoongangCrawlerHelper.getJoongangArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }

    @Scheduled(cron = "0 20 0/6 * * *")
    public void getKhanArticles() {
        log.info("경향 기사 수집 시작");
        List<Map<String, String>> articles = KhanCrawlerHelper.collectAllArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }

    @Scheduled(cron = "0 30 0/6 * * *")
    public void getYtnArticles() {
        log.info("YTN 기사 수집 시작");
        List<Map<String, String>> articles = YtnNewsCrawlerHelper.collectArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }

    @Scheduled(cron = "0 40 0/6 * * *")
    public void getDongaArticles() {
        log.info("동아일보 기사 수집 시작");
        List<Map<String, String>> articles = DongaCrawlerHelper.collectArticles();
        saveToDatabase(articles, transactionManager, newsRepository, newsDocumentRepository);
    }
}
