package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tjeoun.newssearch.dto.AdminNewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminNewsService {

  private final NewsRepository newsRepository;

  public Page<AdminNewsDto> getNewsPage(int page, int size, String category, String mediaCompany) {
    Page<News> news;
    if ("ALL".equals(category) && "ALL".equals(mediaCompany)) {
      news = newsRepository.findByIs_blindFalse(PageRequest.of(page, size));
    } else if (!"ALL".equals(category) && "ALL".equals(mediaCompany)) {
      news = newsRepository.findByCategoryAndIs_blindFalse(NewsCategory.valueOf(category), PageRequest.of(page, size));
    } else if ("ALL".equals(category)) {
      news = newsRepository.findByMediaCompanyAndIs_blindFalse(NewsMediaCompany.valueOf(mediaCompany), PageRequest.of(page, size));
    } else {
      news = newsRepository.findByCategoryAndMediaCompanyAndIs_blindFalse(
        NewsCategory.valueOf(category),
        NewsMediaCompany.valueOf(mediaCompany),
        PageRequest.of(page, size)
      );
    }

    return news.map(AdminNewsDto::fromEntity);
  }

  public AdminNewsDto getNewsDto(Long id) {
    News news = newsRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("News not found"));
    return AdminNewsDto.fromEntity(news);
  }

  @Transactional
  public void updateNews(Long id, AdminNewsDto dto) {
    News news = newsRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("News not found"));
    news.setTitle(dto.getTitle());
    news.setContent(dto.getContent());
    news.setCategory(dto.getCategory());
    news.setMediaCompany(dto.getMediaCompany());
    news.setImageUrl(dto.getImageUrl());
    news.setUrl(dto.getUrl());
  }

  @Transactional
  public void softDeleteNews(Long id) {
    News news = newsRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("News not found"));
    news.set_blind(true);
  }

}

