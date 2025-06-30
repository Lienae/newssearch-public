package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.service.YtnNewsCollectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Ytn")
public class YtnController {
  private final YtnNewsCollectorService ytnNewsCollectorService;
  @PostMapping("/test")
  public ResponseEntity<String> collectYtn(){
    ytnNewsCollectorService.collectAndSaveArticles();
    return ResponseEntity.ok("수집 완료");
  }
}
