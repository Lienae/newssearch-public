package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import com.tjeoun.newssearch.service.KhanRssCollectorService;
import com.tjeoun.newssearch.util.RssUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = KhanNewsController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {KhanNewsController.class})
public class KhanNewsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private KhanRssCollectorService collectorService;

  @MockBean
  private RssUtils rssUtils;

  @MockBean
  private NewsRepository newsRepository;

  @MockBean
  private NewsDocumentRepository newsDocumentRepository;

  @Test
  @DisplayName("뉴스 수집 API 응답이 200 OK인지 확인")
  void collectNewsTest() throws Exception {
    doNothing().when(collectorService).collectAndSaveArticles();

    mockMvc.perform(post("/api/khan/collect")
        .contentType("application/json"))
      .andExpect(status().isOk())
      .andExpect(content().string("수집 완료"));
  }
}

