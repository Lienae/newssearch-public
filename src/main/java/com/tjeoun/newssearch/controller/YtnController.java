package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.service.NewsCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Ytn")
public class YtnController {
    private final NewsCrawlerService newsCrawlerService;
    @PostMapping("/test")
    public ResponseEntity<String> collectYtn(){
        newsCrawlerService.getYtnArticles();
        return ResponseEntity.ok("수집 완료");
    }
}
