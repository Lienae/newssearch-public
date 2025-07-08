package com.tjeoun.newssearch.controller;


import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.dto.AdminJobDto;
import com.tjeoun.newssearch.dto.AdminJobUpdateDto;
import com.tjeoun.newssearch.dto.AdminNewsDto;
import com.tjeoun.newssearch.service.AdminBoardService;
import com.tjeoun.newssearch.service.AdminJobService;
import com.tjeoun.newssearch.service.AdminNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/main")
@RequiredArgsConstructor
public class AdminMainController {

  private final AdminBoardService adminBoardService;
  private final AdminNewsService adminNewsService;
  private final AdminJobService adminJobService;

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
    List<AdminJobDto> jobList = adminJobService.getAllJobsAsDto();
    model.addAttribute("jobList", jobList);
    model.addAttribute("totalCount", jobList.size());
    return "admin/admin-job";
  }

  @PostMapping("/job/update")
  public String updateJobStatus(@ModelAttribute AdminJobUpdateDto dto, RedirectAttributes redirectAttributes) {
    adminJobService.updateJobStatus(dto.getId(), dto.getIsResolved());
    return "redirect:/admin/main/job";
  }

}

