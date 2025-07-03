package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.AdminNewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.service.NewsService;
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

  private final NewsService newsService;

  @GetMapping("/list")
  public String list(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(defaultValue = "ALL") String category,
                     @RequestParam(defaultValue = "ALL") String mediaCompany,
                     Model model) {
    Page<AdminNewsDto> newsPage = newsService.getNewsPage(page, size, category, mediaCompany);
    model.addAttribute("newsPage", newsPage);
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    model.addAttribute("currentCategory", category);
    model.addAttribute("currentMediaCompany", mediaCompany);
    model.addAttribute("totalCount", newsPage.getTotalElements());
    return "admin/news-list";
  }

  @GetMapping("/edit")
  public String editForm(@RequestParam Long id,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(defaultValue = "ALL") String category,
                         @RequestParam(defaultValue = "ALL") String mediaCompany,
                         Model model) {
    AdminNewsDto dto = newsService.getNewsDto(id);
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
    newsService.updateNews(id, dto);
    return "redirect:/admin/news/list?page=" + page + "&size=" + size +
      "&category=" + category + "&mediaCompany=" + mediaCompany;
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    newsService.softDeleteNews(id);
    return "redirect:/admin/news/list";
  }
}

