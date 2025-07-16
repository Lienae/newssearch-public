package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.AdminJobDto;
import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.service.AdminJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<AdminJobDto>> getJobs() {
        List<AdminJobDto> jobs = adminJobService.getJobs();

        if(jobs.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(jobs); // 200 OK
    }
}
