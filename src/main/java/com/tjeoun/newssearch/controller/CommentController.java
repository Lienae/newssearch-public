package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.NewsReplyDto;
import com.tjeoun.newssearch.service.NewsCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final NewsCommentService commentService;

    // 댓글 작성: POST /api/v1/comment/create
    @PostMapping("/create")
    public ResponseEntity<NewsReplyDto> createComment(
            @RequestBody NewsReplyDto dto
            // @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 로그인 기능 미완성으로 테스트용 이메일
        String email = "test@example.com";
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

    // 댓글 목록 조회: GET /api/v1/comment/list?url={뉴스URL}
    @GetMapping("/list")
    public ResponseEntity<List<NewsReplyDto>> getCommentsByUrl(@RequestParam String url) {
        List<NewsReplyDto> replies = commentService.findCommentsByNewsUrl(url);
        return ResponseEntity.ok(replies);
    }
}