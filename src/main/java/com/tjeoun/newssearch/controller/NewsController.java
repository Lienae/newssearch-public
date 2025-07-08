package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsRepository newsRepository;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0", name = "page") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "ALL") String category,
                       @RequestParam(defaultValue = "ALL") String mediaCompany,
                       Model model) {

        // 파라미터 정리 (소문자 → 대문자 보정)
        category = category.toUpperCase();
        mediaCompany = mediaCompany.toUpperCase();

        Page<News> news;

        // 분기 조건 동일하게 유지
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

        Page<NewsDto> newsPage = news.map(newsEntity -> NewsDto.builder()
                .id(newsEntity.getId())
                .url(newsEntity.getUrl())
                .title(newsEntity.getTitle())
                .content(newsEntity.getContent())
                .imageUrl(newsEntity.getImageUrl())
                .publishDate(newsEntity.getPublishDate())
                .mediaCompany(newsEntity.getMediaCompany())
                .category(newsEntity.getCategory())
                .author(newsEntity.getAuthor())
                .build());

        model.addAttribute("newsPage", newsPage);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentMediaCompany", mediaCompany);
        model.addAttribute("totalCount", newsPage.getTotalElements());

        return "news/news-list";
    }

    @GetMapping("/edit")
    public String editForm(@RequestParam Long id,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           @RequestParam(defaultValue = "ALL") String category,
                           @RequestParam(defaultValue = "ALL") String mediaCompany,
                           Model model) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        NewsDto dto = NewsDto.builder()
                .id(news.getId())
                .content(news.getContent())
                .publishDate(news.getPublishDate())
                .build();

        model.addAttribute("news", dto);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentMediaCompany", mediaCompany);
        return "admin/news-edit";
    }

}