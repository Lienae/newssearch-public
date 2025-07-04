package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.AdminNewsDto;
import com.tjeoun.newssearch.service.AdminNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

  private final AdminNewsService adminNewsService;

  @GetMapping("/list")
  public String list(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(defaultValue = "ALL") String category,
                     @RequestParam(defaultValue = "ALL") String mediaCompany,
                     Model model) {
    Page<AdminNewsDto> newsPage = adminNewsService.getNewsPage(page, size, category, mediaCompany);
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
    AdminNewsDto dto = adminNewsService.getNewsDto(id);
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
                     @RequestParam("filterCategory") String filterCategory,
                     @RequestParam("filterMediaCompany") String filterMediaCompany,
                     @ModelAttribute AdminNewsDto dto,
                     @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
    adminNewsService.updateNewsWithFile(id, dto, file);
    return "redirect:/admin/news/list?page=" + page + "&size=" + size +
      "&category=" + filterCategory  + "&mediaCompany=" + filterMediaCompany;
  }


  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    adminNewsService.softDeleteNews(id);
    return "redirect:/admin/news/list";
  }
}

