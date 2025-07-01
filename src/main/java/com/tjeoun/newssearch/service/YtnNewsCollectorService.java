package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.crawler.YtnNewsCrawler;
import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class YtnNewsCollectorService {

  private final NewsRepository newsRepository;
  private final NewsDocumentRepository newsDocumentRepository;
  private final YtnNewsCrawler crawler;

  public void collectAndSaveArticles() {
    String[] categories = { "0101", "0102", "0103", "0106", "0107" };
    int targetCount = 20;

    for (String mcd : categories) {
      int page = 1, count = 0;
      // 중복 방지용 세트/리스트
      List<String> pivots = new ArrayList<>();
      Set<String> seenJoinKeys = new HashSet<>();

      while (count < targetCount) {
        List<Map<String, String>> articles = crawler.fetchNewsList(mcd, page,pivots);
        if (articles.isEmpty()) break;

        for (Map<String, String> item : articles) {
          String joinKey = item.get("join_key");
          String title = item.get("title");

          // 중복 확인
          if (joinKey == null || seenJoinKeys.contains(joinKey)) continue;

          // 본문 + 날짜 + 기자 + 썸네일 크롤링
          NewsDto dto = crawler.crawlAndConvertToDto(mcd, joinKey, title);
          if (dto == null) {
            pivots.add(joinKey); // 실패했어도 pivot으로 등록
            continue;
          }

          // DB 저장
          News newsEntity = News.createNewsFromDto(dto);
          News savedNews = newsRepository.save(newsEntity);

          // ES 저장
          NewsDocument document = News.toDocument(savedNews);
          newsDocumentRepository.save(document);

          log.info("✅ YTN 기사 저장 완료: {}", dto.getTitle());

          // 등록된 기사 처리
          seenJoinKeys.add(joinKey);
          pivots.add(joinKey);
          count++;
          if (count >= targetCount) break;
        }
        page++;
      }
    }
  }
}
