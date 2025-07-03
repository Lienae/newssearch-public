package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.AdminNewsDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

  private final NewsRepository newsRepository;

  @GetMapping("/list")
  public String list(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(defaultValue = "ALL") String category,
                     @RequestParam(defaultValue = "ALL") String mediaCompany,
                     Model model) {
    Page<News> news;

    if("ALL".equals(category) && "ALL".equals(mediaCompany)){
      news = newsRepository.findByIs_blindFalse(PageRequest.of(page, size));
    } else if(!"ALL".equals(category) && "ALL".equals(mediaCompany)){
      news = newsRepository.findByCategoryAndIs_blindFalse(NewsCategory.valueOf(category),PageRequest.of(page, size));
    } else if("ALL".equals(category)){
      news = newsRepository.findByMediaCompanyAndIs_blindFalse(NewsMediaCompany.valueOf(mediaCompany), PageRequest.of(page, size));
    } else {
      news = newsRepository.findByCategoryAndMediaCompanyAndIs_blindFalse(NewsCategory.valueOf(category), NewsMediaCompany.valueOf(mediaCompany), PageRequest.of(page, size));
    }

    Page<AdminNewsDto> newsPage = news.map(newsEntity -> AdminNewsDto.builder()
      .id(newsEntity.getId())
      .url(newsEntity.getUrl())
      .title(newsEntity.getTitle())
      .imageUrl(newsEntity.getImageUrl())
      .content(newsEntity.getContent())
      .publishDate(newsEntity.getPublishDate())
      .author(newsEntity.getAuthor())
      .category(newsEntity.getCategory())
      .mediaCompany(newsEntity.getMediaCompany())
      .build());

    long totalCount = newsPage.getTotalElements();

    model.addAttribute("newsPage", newsPage);
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    model.addAttribute("currentCategory", category);
    model.addAttribute("currentMediaCompany", mediaCompany);
    model.addAttribute("totalCount", totalCount);
    return "admin/news-list";
  }

}
