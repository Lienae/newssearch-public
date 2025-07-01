package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import com.tjeoun.newssearch.util.RssUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class KhanRssCollectorService {

  private final NewsRepository newsRepository;
  private final NewsDocumentRepository newsDocumentRepository;
  private final RssUtils rssUtils;

  public void collectAndSaveArticles() {
    // 카테고리당 최대 30개 수집
    List<NewsDto> articles = rssUtils.collectAllArticles(30);

    for (NewsDto dto : articles) {
      // DB 저장
      News newsEntity = News.createNewsFromDto(dto);
      News saved = newsRepository.save(newsEntity);

      // ES 저장
      NewsDocument doc = News.toDocument(saved);
      newsDocumentRepository.save(doc);
    }
  }
}