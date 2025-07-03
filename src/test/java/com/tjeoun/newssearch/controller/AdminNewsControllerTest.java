package com.tjeoun.newssearch.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.repository.NewsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminNewsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private NewsRepository newsRepository;

  private News createNews() {
    return News.builder()
      .id(1L)
      .title("테스트 뉴스")
      .author("기자명")
      .content("내용")
      .category(NewsCategory.POLITICS)
      .mediaCompany(NewsMediaCompany.YTN)
      .url("http://test.com")
      .imageUrl("http://test.com/image.jpg")
      .publishDate(LocalDateTime.now())
      .is_blind(false)
      .build();
  }

  @Test
  @DisplayName("뉴스 목록 조회")
  void testList() throws Exception {
    News news = createNews();
    Page<News> page = new PageImpl<>(List.of(news), PageRequest.of(0, 10), 1);

    given(newsRepository.findByIs_blindFalse(any(Pageable.class))).willReturn(page);

    mockMvc.perform(get("/admin/news/list")
        .param("page", "0")
        .param("size", "10")
        .param("category", "ALL")
        .param("mediaCompany", "ALL"))
      .andExpect(status().isOk())
      .andExpect(view().name("admin/news-list"))
      .andExpect(model().attributeExists("newsPage"))
      .andExpect(model().attributeExists("page"))
      .andExpect(model().attributeExists("size"))
      .andExpect(model().attributeExists("currentCategory"))
      .andExpect(model().attributeExists("currentMediaCompany"))
      .andExpect(model().attributeExists("totalCount"));
  }

  @Test
  @DisplayName("뉴스 수정 폼 조회")
  void testEditForm() throws Exception {
    News news = createNews();
    given(newsRepository.findById(1L)).willReturn(Optional.of(news));

    mockMvc.perform(get("/admin/news/edit")
        .param("id", "1")
        .param("page", "0")
        .param("size", "10")
        .param("category", "ALL")
        .param("mediaCompany", "ALL"))
      .andExpect(status().isOk())
      .andExpect(view().name("admin/news-edit"))
      .andExpect(model().attributeExists("news"))
      .andExpect(model().attribute("page", 0))
      .andExpect(model().attribute("size", 10))
      .andExpect(model().attribute("currentCategory", "ALL"))
      .andExpect(model().attribute("currentMediaCompany", "ALL"));
  }

  @Test
  @DisplayName("뉴스 수정 처리")
  void testEdit() throws Exception {
    News news = createNews();
    given(newsRepository.findById(1L)).willReturn(Optional.of(news));

    mockMvc.perform(post("/admin/news/edit/1")
        .with(csrf())
        .param("page", "0")
        .param("size", "10")
        .param("title", "수정된 제목")
        .param("content", "수정된 내용")
        .param("imageUrl", "http://test.com/newimage.jpg")
        .param("url", "http://test.com/newurl")
        .param("category", "POLITICS")
        .param("mediaCompany", "YTN"))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/admin/news/list?page=0&size=10&category=POLITICS&mediaCompany=YTN"));

    then(newsRepository).should().save(any(News.class));
  }

  @Test
  @DisplayName("뉴스 삭제 처리")
  void testDelete() throws Exception {
    News news = createNews();  // 초기값은 is_blind = false
    given(newsRepository.findById(1L)).willReturn(Optional.of(news));

    mockMvc.perform(post("/admin/news/delete/1")
        .with(csrf()))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/admin/news/list"));

    then(newsRepository).should().save(news);
    assertTrue(news.is_blind(), "뉴스가 블라인드 처리되지 않았습니다.");
  }


}
