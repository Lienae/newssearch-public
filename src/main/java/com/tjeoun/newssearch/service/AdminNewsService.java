package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tjeoun.newssearch.dto.AdminNewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminNewsService {

  private final NewsRepository newsRepository;
  private final AttachFileService attachFileService;

  public Page<AdminNewsDto> getNewsPage(int page, int size, String category, String mediaCompany) {
    Pageable pageable = PageRequest.of(page, size);
    Page<News> news;

    boolean isAllCategory = "ALL".equals(category);
    boolean isAllMedia = "ALL".equals(mediaCompany);

    if (isAllCategory && isAllMedia) {
      news = newsRepository.findByIs_blindFalse(pageable);
    } else if (!isAllCategory && isAllMedia) {
      NewsCategory newsCategory = NewsCategory.valueOf(category);
      news = newsRepository.findByCategoryAndIs_blindFalse(newsCategory, pageable);
    } else if (isAllCategory) {
      NewsMediaCompany newsMedia = NewsMediaCompany.valueOf(mediaCompany);
      news = newsRepository.findByMediaCompanyAndIs_blindFalse(newsMedia, pageable);
    } else {
      NewsCategory newsCategory = NewsCategory.valueOf(category);
      NewsMediaCompany newsMedia = NewsMediaCompany.valueOf(mediaCompany);
      news = newsRepository.findByCategoryAndMediaCompanyAndIs_blindFalse(newsCategory, newsMedia, pageable);
    }

    return news.map(AdminNewsDto::fromEntity);
  }


  public AdminNewsDto getNewsDto(Long id) {
    News news = newsRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("News not found"));
    return AdminNewsDto.fromEntity(news);
  }

  @Transactional
  public void updateNewsWithFile(Long id, AdminNewsDto dto, MultipartFile file) throws Exception {
    News news = newsRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("News not found"));
    news.setTitle(dto.getTitle());
    news.setContent(dto.getContent());
    news.setCategory(dto.getCategory());
    news.setMediaCompany(dto.getMediaCompany());
    // news.setImageUrl(dto.getImageUrl());
    news.setUrl(dto.getUrl());

    // 기존 이미지 파일 삭제 (파일명 추출은 imageUrl에서 경로 분리 필요)
    if (news.getImageUrl() != null && file != null && !file.isEmpty()) {
      String oldFileName = extractFileNmaeFromUrl(news.getImageUrl());
      attachFileService.deleteFile(oldFileName);
    }

    // 새 이미지 파일 저장
    if (file != null && !file.isEmpty()) {
      String serverFilename = attachFileService.saveFile(file.getOriginalFilename(), file.getBytes());
      String imageUrl = "/images/upload/" + serverFilename;
      news.setImageUrl(imageUrl);
    }

  }

  private String extractFileNmaeFromUrl(String imageUrl) {
    if (imageUrl == null) return null;
    int idx = imageUrl.lastIndexOf("/");
    return idx >= 0 ? imageUrl.substring(idx + 1) : imageUrl;
  }

  @Transactional
  public void softDeleteNews(Long id) {
    News news = newsRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("News not found"));
    news.set_blind(true);
  }

  public List<AdminNewsDto> getRecentNewsList() {
    return newsRepository.findTop5ByOrderByPublishDateDesc()
      .stream()
      .map(AdminNewsDto::fromEntity)
      .toList();
  }
}

