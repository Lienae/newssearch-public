package com.tjeoun.newssearch.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class YtnNewsCollectorServiceTest {

  @Autowired
  private YtnNewsCollectorService newsService;


  // 크롤링 + 저장 테스트
  @Test
  public void testCollectAndSaveArticles() {
    newsService.collectAndSaveArticles();  // 메서드명에 맞게 호출
  }

}