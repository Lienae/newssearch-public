package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.NewsDto;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0", name = "page") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "ALL") String category,
                       @RequestParam(defaultValue = "ALL") String mediaCompany,
                       @RequestParam(required = false) String url,
                       Model model) {

        Page<NewsDto> newsPage = newsService.getNewsList(page, size, category, mediaCompany);

        model.addAttribute("newsPage", newsPage);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentMediaCompany", mediaCompany);
        model.addAttribute("totalCount", newsPage.getTotalElements());

        if (url != null) {
            model.addAttribute("popupUrl", url);
        }

        return "news/news-list";
    }
}
