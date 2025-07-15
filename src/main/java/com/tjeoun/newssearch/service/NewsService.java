package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public Page<NewsDto> getNewsList(int page, int size, String category, String mediaCompany) {
        category = category.toUpperCase();
        mediaCompany = mediaCompany.toUpperCase();

        Page<News> news;

        if ("ALL".equals(category) && "ALL".equals(mediaCompany)) {
            news = newsRepository.findByIsBlindFalse(PageRequest.of(page, size));
        } else if (!"ALL".equals(category) && "ALL".equals(mediaCompany)) {
            news = newsRepository.findByCategoryAndIsBlindFalse(NewsCategory.valueOf(category), PageRequest.of(page, size));
        } else if ("ALL".equals(category)) {
            news = newsRepository.findByMediaCompanyAndIsBlindFalse(NewsMediaCompany.valueOf(mediaCompany), PageRequest.of(page, size));
        } else {
            news = newsRepository.findByCategoryAndMediaCompanyAndIsBlindFalse(
                    NewsCategory.valueOf(category), NewsMediaCompany.valueOf(mediaCompany), PageRequest.of(page, size));
        }

        return news.map(NewsDto::fromEntity); // DTO로 변환
    }

    public NewsDto getNewsById(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        return NewsDto.builder()
                .id(news.getId())
                .content(news.getContent())
                .publishDate(news.getPublishDate())
                .build();
    }
    public News findById(Long id) {
        return newsRepository.findById(id).orElseThrow(() -> new RuntimeException("News not found"));
    }
}
