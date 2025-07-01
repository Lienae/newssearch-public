package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.service.NewsCollectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/donga")
public class DongaNewsController {
    private final NewsCollectorService newsCollectorService;

    @PostMapping("/collect") // POST 방식으로 수집
    public ResponseEntity<String> collectDongaNews() {
        newsCollectorService.collectAndSaveArticles();
        return ResponseEntity.ok("동아일보 뉴스 수집 및 DB 저장 완료!");
    }
}
