package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.NewsReplyDto;
import com.tjeoun.newssearch.enums.ReportEnum;
import com.tjeoun.newssearch.service.NewsCommentService;
import com.tjeoun.newssearch.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final NewsCommentService commentService;
    private final ReportService reportService;
    // 댓글 작성: POST /api/v1/comment/create
    @PostMapping("/create")
    public ResponseEntity<NewsReplyDto> createComment(
            @RequestBody NewsReplyDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        return ResponseEntity.ok(commentService.addComment(dto, email));
    }

    // 댓글 삭제: POST /api/v1/comment/remove
    @PostMapping("/remove")
    public ResponseEntity<String> removeComment(
            @RequestParam Long commentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        commentService.deleteComment(commentId, email);
        return ResponseEntity.ok("댓글 삭제 완료 (isBlind = true)");
    }

    // 댓글 수정
    @PostMapping("/update")
    public ResponseEntity<String> updateComment(
            @RequestBody NewsReplyDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String email = userDetails.getUsername();
        commentService.updateComment(dto.getId(), dto.getContent(), email);
        return ResponseEntity.ok("댓글 수정 완료");
    }

    // 댓글 목록 조회: GET /api/v1/comment/list?url={뉴스URL}
    @GetMapping("/list")
    public ResponseEntity<List<NewsReplyDto>> getCommentsByUrl(@RequestParam String url) {
        List<NewsReplyDto> replies = commentService.findCommentsByNewsUrl(url);
        return ResponseEntity.ok(replies);
    }

    // 댓글 신고
    @PostMapping("/report")
    public ResponseEntity<String> reportComment(
            @RequestParam Long commentId,
            Principal principal
    ) {
        reportService.save(commentId, principal.getName(), ReportEnum.NEWS_REPLY);
        System.out.println("신고 접수됨 - 댓글 ID: " + commentId + ", 신고자: " + principal.getName());

        return ResponseEntity.ok("신고 접수 완료");
    }

}