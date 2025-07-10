package com.tjeoun.newssearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
  @PostMapping("/board/{boardId}")
  public ResponseEntity<?> reportBoard(@PathVariable Long boardId) {
    System.out.println("게시글 신고됨: " + boardId);
    return ResponseEntity.ok().body("게시글이 신고되었습니다.");
  }

  @PostMapping("/reply/{replyId}")
  public ResponseEntity<?> reportReply(@PathVariable Long replyId) {
    System.out.println("댓글 신고됨: " + replyId);
    return ResponseEntity.ok().body("댓글이 신고되었습니다.");
  }
}
