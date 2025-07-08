package com.tjeoun.newssearch.controller;


import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.dto.AdminNewsDto;
import com.tjeoun.newssearch.service.AdminBoardService;
import com.tjeoun.newssearch.service.AdminNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin/main")
@RequiredArgsConstructor
public class AdminMainController {

  private final AdminBoardService adminBoardService;
  private final AdminNewsService adminNewsService;
  // 필요시 다른 서비스도 주입

  @GetMapping
  public String dashboard(Model model) {
    // 게시판 최근글 5개
    List<AdminBoardDto> recentBoardList = adminBoardService.getRecentBoardList();

    // 최근 등록 뉴스 기사 5개 (카테고리/언론사별) 필터링해야 될 듯?
    List<AdminNewsDto> recentNewsList = adminNewsService.getRecentNewsList();


    model.addAttribute("recentBoardList", recentBoardList);
    model.addAttribute("recentNews", recentNewsList);

    return "admin/main";
  }

  @GetMapping("/job")
  public String job(Model model) {
    return "admin/admin-job";
  }
}

