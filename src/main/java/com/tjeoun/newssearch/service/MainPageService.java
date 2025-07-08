package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// MainNewsDto 같은 DTO가 있다면 쓰고, 없다면 News 엔티티 그대로 반환해도 OK
@Service
@RequiredArgsConstructor
public class MainPageService {

    private final NewsRepository newsRepository;

    public Map<NewsCategory, List<NewsDto>> getTop2NewsByCategory() {
        Map<NewsCategory, List<NewsDto>> result = new LinkedHashMap<>();

        for (NewsCategory category : NewsCategory.values()) {
            List<NewsDto> top2 = newsRepository.findByCategoryAndIsBlindFalseOrderByPublishDateDesc(category, PageRequest.of(0, 2))
                    .stream()
                    .map(NewsDto::fromEntity)
                    .toList();
            result.put(category, top2);
        }

        return result;
    }

    public Map<NewsMediaCompany, List<NewsDto>> getTop2NewsByMediaCompany() {
        Map<NewsMediaCompany, List<NewsDto>> result = new LinkedHashMap<>();

        for (NewsMediaCompany company : NewsMediaCompany.values()) {
            List<NewsDto> top2 = newsRepository.findByMediaCompanyAndIsBlindFalseOrderByPublishDateDesc(company, PageRequest.of(0, 2))
                    .stream()
                    .map(NewsDto::fromEntity)
                    .toList();
            result.put(company, top2);
        }

        return result;
    }

}