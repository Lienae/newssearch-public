package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.service.BoardSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardSearchController {
  private final BoardSearchService boardSearchService;

  @GetMapping("/search")
  public String search(@RequestParam("keyword") String keyword, Model model) {
    List<BoardDocument> results = boardSearchService.search(keyword);
    model.addAttribute("results", results);
    model.addAttribute("keyword", keyword);
    return "board/search-result";
  }
}
