package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.service.KhanRssCollectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/khan")
public class KhanNewsController {

  private final KhanRssCollectorService collectorService;

  @PostMapping("/collect")
  public ResponseEntity<String> collectNews() {
    collectorService.collectAndSaveArticles();
    return ResponseEntity.ok("수집 완료");
  }
}
