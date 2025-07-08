package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/main")
public class MainPageController {

    private final MainPageService mainPageService;

    @GetMapping
    public String mainPage(Model model) {
        Map<NewsCategory, List<NewsDto>> newsByCategory = mainPageService.getTop2NewsByCategory();
        model.addAttribute("newsByCategory", newsByCategory);

        Map<NewsMediaCompany, List<NewsDto>> newsByCompany = mainPageService.getTop2NewsByMediaCompany();
        model.addAttribute("newsByCompany", newsByCompany);

        return "main";
    }
}