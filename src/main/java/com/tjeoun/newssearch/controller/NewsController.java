package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.dto.NewsReplyDto;
import com.tjeoun.newssearch.entity.News;

import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.service.NewsCommentService;

import com.tjeoun.newssearch.service.NewsSearchService;
import com.tjeoun.newssearch.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final NewsSearchService newsSearchService;
    private final NewsRepository newsRepository;
    private final NewsCommentService newsCommentService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(defaultValue = "ALL") String category,
                       @RequestParam(defaultValue = "ALL") String mediaCompany,
                       @RequestParam(required = false) String url,
                       @RequestParam(required = false) String query,
                       Model model) {

        Page<NewsDto> newsPage;

        if (query != null && !query.isEmpty()) {
            newsPage = newsSearchService.search(query, category, mediaCompany, page, size);
        } else {
            newsPage = newsService.getNewsList(page, size, category, mediaCompany);
        }

        model.addAttribute("newsPage", newsPage);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentMediaCompany", mediaCompany);
        model.addAttribute("query", query);
        model.addAttribute("totalCount", newsPage.getTotalElements());

        if (url != null) {
            model.addAttribute("popupUrl", url);
        }
        return "news/news-list";
    }


    @GetMapping("/view")
    public String view(@RequestParam Long id) {
        News news = newsService.findById(id);
        return "redirect:/news/list?category=" + news.getCategory()
                + "&mediaCompany=" + news.getMediaCompany()
                + "&url=" + news.getUrl();
    }

}
