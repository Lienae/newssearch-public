package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.enums.ReportEnum;
import com.tjeoun.newssearch.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {
    private final ReportService reportService;
    @PostMapping("/board/{boardId}")
    public ResponseEntity<?> reportBoard(@PathVariable Long boardId, Principal principal) {
        reportService.save(boardId, principal.getName(), ReportEnum.BOARD);
        System.out.println("게시글 신고됨: " + boardId);
        return ResponseEntity.ok().body("게시글이 신고되었습니다.");

    }

    @PostMapping("/reply/{replyId}")
    public ResponseEntity<?> reportReply(@PathVariable Long replyId, Principal principal) {
        reportService.save(replyId, principal.getName(), ReportEnum.BOARD_REPLY);
        System.out.println("댓글 신고됨: " + replyId);
        return ResponseEntity.ok().body("댓글이 신고되었습니다.");
    }
}
