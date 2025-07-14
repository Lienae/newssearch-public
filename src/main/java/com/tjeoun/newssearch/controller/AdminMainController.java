package com.tjeoun.newssearch.controller;


import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.dto.AdminJobDto;
import com.tjeoun.newssearch.dto.AdminJobUpdateDto;
import com.tjeoun.newssearch.dto.AdminNewsDto;
import com.tjeoun.newssearch.service.AdminBoardService;
import com.tjeoun.newssearch.service.AdminJobService;
import com.tjeoun.newssearch.service.AdminNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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

        // 최근 등록 뉴스 기사 5개
        List<AdminNewsDto> recentNewsList = adminNewsService.getRecentNewsList();

        // PPT 캡처용
        // List<AdminNewsDto> recentNewsList = adminNewsService.getTestNewsList();


        model.addAttribute("recentBoardList", recentBoardList);
        model.addAttribute("recentNews", recentNewsList);


    return "admin/main";
  }

  @GetMapping("/job")
  public String job(@RequestParam(defaultValue = "0") int page,
                    @RequestParam(defaultValue = "10") int size,
                    @RequestParam(defaultValue = "ALL") String filter,
                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate searchDate,
                    @RequestParam(required = false) Long jobId,
                    Model model) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "recordedTime"));
    Page<AdminJobDto> jobPage = adminJobService.getFilteredJobs(filter, searchDate, pageable);

    model.addAttribute("jobPage", jobPage);
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    model.addAttribute("filter", filter);
    model.addAttribute("totalCount", jobPage.getTotalElements());
    model.addAttribute("highlightJobId", jobId);

    return "admin/admin-job";
  }




  @PostMapping("/job/update")
  public String updateJobStatus(@ModelAttribute AdminJobUpdateDto dto) {
    adminJobService.updateJobStatus(dto.getId(), dto.getIsResolved());
    return "redirect:/admin/main/job";
  }

}

