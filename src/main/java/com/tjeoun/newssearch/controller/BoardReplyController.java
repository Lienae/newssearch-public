package com.tjeoun.newssearch.controller;


import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.service.BoardReplyService;
import com.tjeoun.newssearch.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardReplyController {
  private final BoardReplyService boardReplyService;
  private final MemberService memberService;

  // 댓글 등록
  @PostMapping("/{boardId}/reply")
  public ResponseEntity<?> saveReply(@PathVariable Long boardId,
                                     @RequestParam("commentContent") String content,
                                     Principal principal) {
    Member loginUser = memberService.getLoginMember(principal);
    boardReplyService.saveReply(boardId, content, loginUser);

    long replyCount = boardReplyService.getReplyCountByBoardId(boardId);


    return ResponseEntity.ok().body(Map.of(
      "message", "댓글 등록 성공",
      "replyCount", replyCount));
  }



  // 댓글 수정 폼 열기
  @PostMapping("/reply/editing/{replyId}")
  public String editingReply(@PathVariable Long replyId,
                             @RequestParam Long boardId,
                             Principal principal,
                             Model model) {
    Member loginUser = memberService.getLoginMember(principal);
    boardReplyService.prepareEditReplyPage(replyId, boardId, loginUser, model);
    return "board/board-detail";
  }


  // 댓글 수정 처리
  @PutMapping("/reply/{replyId}")
  public ResponseEntity<?> updateReply(@PathVariable Long replyId,
                                       @RequestParam String content,
                                       Principal principal) {
    Member loginUser = memberService.getLoginMember(principal);
    boardReplyService.updateReply(replyId, content, loginUser);

    return ResponseEntity.ok(Map.of("message", "댓글 수정 성공"));
  }

  // 댓글 삭제 처리
  @DeleteMapping("/reply/{replyId}")
  public ResponseEntity<?> deleteReply(@PathVariable Long replyId, Principal principal, HttpServletRequest request) {
    Member loginUser = memberService.getLoginMember(principal);
    Long boardId = boardReplyService.deleteReply(replyId, loginUser, request);

    long replyCount = boardReplyService.getReplyCountByBoardId(boardId);

    return ResponseEntity.ok(Map.of("message", "댓글 삭제 성공",
      "replyCount", replyCount));
  }

  @GetMapping("/{boardId}/replies")
  public ResponseEntity<?> getReplies(@PathVariable Long boardId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    List<BoardReply> replies = boardReplyService.findRepliesByBoardId(boardId);

    List<Map<String, String>> replyList = replies.stream().map(reply -> Map.of(
      "id", String.valueOf(reply.getId()),
      "author", reply.getMember().getName(),
      "content", reply.getContent(),
      "createdDate", reply.getCreatedDate().format(formatter),
      "memberId", String.valueOf(reply.getMember().getId()),
      "memberName", reply.getMember().getName()
    )).toList();

    return ResponseEntity.ok(Map.of("replies", replyList));
  }







}