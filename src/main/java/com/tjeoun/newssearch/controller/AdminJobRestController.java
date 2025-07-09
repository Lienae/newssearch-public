package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.service.AdminJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AdminJobRestController {

    private final AdminJobService adminJobService;

    @GetMapping("/admin-jobs")
    public List<AdminJob> getRecentJobs() { // 단순 조회용
        return adminJobService.getJobs();
    }
}
