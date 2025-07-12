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
import java.util.stream.Collectors;

// MainNewsDto 같은 DTO가 있다면 쓰고, 없다면 News 엔티티 그대로 반환해도 OK
@Service
@RequiredArgsConstructor
public class MainPageService {

    private final NewsRepository newsRepository;

    public Map<NewsCategory, List<NewsDto>> getTop1NewsByCategory() {
        Map<NewsCategory, List<NewsDto>> result = new LinkedHashMap<>();

        for (NewsCategory category : NewsCategory.values()) {
            if (category == NewsCategory.MISC) continue;
            List<NewsDto> top1 = newsRepository.findByCategoryAndIsBlindFalseOrderByPublishDateDesc(category, PageRequest.of(0, 1))
                    .stream()
                    .map(NewsDto::fromEntity)
                    .collect(Collectors.toList());
            result.put(category, top1);
        }

        return result;
    }

    public Map<NewsMediaCompany, List<NewsDto>> getTop1NewsByMediaCompany() {
        Map<NewsMediaCompany, List<NewsDto>> result = new LinkedHashMap<>();

        for (NewsMediaCompany company : NewsMediaCompany.values()) {
            List<NewsDto> top1 = newsRepository.findByMediaCompanyAndIsBlindFalseOrderByPublishDateDesc(company, PageRequest.of(0, 1))
                    .stream()
                    .map(NewsDto::fromEntity)
                    .toList();
            result.put(company, top1);
        }

        return result;
    }

    public List<NewsDto> getRecentNews() {
        return newsRepository.findTop5ByOrderByPublishDateDesc()
                .stream()
                .map(NewsDto::fromEntity)
                .toList();
    }

}