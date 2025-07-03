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
import org.springframework.web.bind.annotation.*;

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

  @GetMapping("/edit")
  public String editForm(@RequestParam Long id,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(defaultValue = "ALL") String category,
                         @RequestParam(defaultValue = "ALL") String mediaCompany,
                         Model model) {
    News news = newsRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("News not found"));

    AdminNewsDto dto = AdminNewsDto.builder()
      .id(news.getId())
      .title(news.getTitle())
      .author(news.getAuthor())
      .category(news.getCategory())
      .mediaCompany(news.getMediaCompany())
      .content(news.getContent())
      .imageUrl(news.getImageUrl())
      .url(news.getUrl())
      .publishDate(news.getPublishDate())
      .build();

    model.addAttribute("news", dto);
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    model.addAttribute("currentCategory", category);
    model.addAttribute("currentMediaCompany", mediaCompany);
    return "admin/news-edit";
  }

  @PostMapping("/edit/{id}")
  public String edit(@PathVariable Long id,
                     @RequestParam int page,
                     @RequestParam int size,
                     @RequestParam String category,
                     @RequestParam String mediaCompany,
                     @ModelAttribute AdminNewsDto dto){
    News news = newsRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("News not found"));

    news.setTitle(dto.getTitle());
    news.setContent(dto.getContent());
    news.setCategory(dto.getCategory());
    news.setMediaCompany(dto.getMediaCompany());
    news.setImageUrl(dto.getImageUrl());
    news.setUrl(dto.getUrl());

    newsRepository.save(news);

    return "redirect:/admin/news/list?page=" + page + "&size=" + size +
      "&category=" + category + "&mediaCompany=" + mediaCompany;
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    News news = newsRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("News not found"));
    news.set_blind(true);
    newsRepository.save(news);
    return "redirect:/admin/news/list";
  }
}
