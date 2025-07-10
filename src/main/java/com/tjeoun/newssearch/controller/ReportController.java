package com.tjeoun.newssearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {
    @PostMapping("/board/{boardId}")
    public ResponseEntity<?> reportBoard(@PathVariable Long boardId, @RequestBody Map<String, Long> memberId) {
        System.out.println("게시글 신고됨: " + boardId);
        return ResponseEntity.ok().body("게시글이 신고되었습니다.");
    }

    @PostMapping("/reply/{replyId}")
    public ResponseEntity<?> reportReply(@PathVariable Long replyId, @RequestBody Map<String, Long> memberId) {
        System.out.println("댓글 신고됨: " + replyId);
        return ResponseEntity.ok().body("댓글이 신고되었습니다.");
    }
}
