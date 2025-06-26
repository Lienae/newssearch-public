package com.tjeoun.newssearch.util;

import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.enums.NewsCategory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RssUtilsTest {

  private static final Logger log = LoggerFactory.getLogger(RssUtilsTest.class);

  @Autowired
  private RssUtils rssUtils;

  @Test
  void testCollectAllArticles() {
    List<NewsDto> articles = rssUtils.collectAllArticles(5);

    assertThat(articles).isNotEmpty();
    assertThat(articles.size()).isLessThanOrEqualTo(5 * 5); // 최대 카테고리 5개 x 5개

    for (NewsDto article : articles) {
      log.info("[{}] {}", article.getCategory(), article.getTitle());
      assertThat(article.getTitle()).isNotBlank();
      assertThat(article.getContent()).isNotBlank();
      assertThat(article.getAuthor()).isNotBlank();
      assertThat(article.getPublishDate()).isNotNull();
      assertThat(article.getMediaCompany()).isNotNull();
    }
  }

  @Test
  void testCollectFromSingleCategory() {
    List<NewsDto> politics = rssUtils.collectAllArticles(1)
      .stream()
      .filter(dto -> dto.getCategory() == NewsCategory.POLITICS)
      .toList();

    assertThat(politics).isNotEmpty();
    politics.forEach(dto -> {
      log.info("POLITICS >> {}", dto.getTitle());
      assertThat(dto.getCategory()).isEqualTo(NewsCategory.POLITICS);
    });
  }
}
