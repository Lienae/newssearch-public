package com.tjeoun.newssearch.controller;

import co.elastic.clients.elasticsearch.nodes.Http;
import com.tjeoun.newssearch.dto.BoardReplyDto;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.MemberRepository;
import com.tjeoun.newssearch.service.BoardReplyService;
import com.tjeoun.newssearch.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boarder")
public class BoardReplyController {
  private final BoardReplyService boardReplyService;
  private final BoardService boardService;
  private final MemberRepository memberRepository;
  private final BoardReplyRepository boardReplyRepository;

  @PostMapping("/{boardId}/reply")
  public String saveReply(@PathVariable Long boardId,
                          @RequestParam("commentContent") String content,
                          HttpSession session) {
    Member loginMember = (Member) session.getAttribute("loginUser");
    if (loginMember == null) {
      // 로그인 안 되어있으면 id=1 회원을 임의로 가져오기 (익명 사용자)
      loginMember = memberRepository.findById(3L)
        .orElseThrow(() -> new IllegalStateException("기본 사용자(익명 사용자)를 찾을 수 없습니다."));
    }
    Board board = boardService.findById(boardId);  // 게시글 찾아오기

    BoardReplyDto dto = BoardReplyDto.builder()
      .content(content)
      .board(board)
      .member(loginMember)
      .build();

    boardReplyService.saveReply(dto);

    return "redirect:/boarder/detail/" + boardId;
  }


  @PostMapping("/reply/editing/{replyId}")
  public String editingReply(@PathVariable Long replyId,
                             @RequestParam Long boardId,
                             HttpSession session,
                             Model model) {

    // 댓글 목록
    List<BoardReply> replies = boardReplyService.findRepliesByBoardId(boardId);
    Member loginUser = (Member) session.getAttribute("loginUser");

    // 수정 중인 댓글 ID를 리스트로 넘김
    model.addAttribute("editingReplies", List.of(replyId));
    // 게시글 정보도 필요
    Board board = boardService.findById(boardId);
    model.addAttribute("board", board);
    // 댓글 목록 전달
    model.addAttribute("replies", replies);
    // 의견 개수
    model.addAttribute("replyCount", replies.size());

    model.addAttribute("loginUser", loginUser);

    // ✅ 댓글 개수 → 수정
    long visibleReplyCount = boardReplyService.countVisibleReplies(boardId);
    model.addAttribute("replyCount", visibleReplyCount);

    return "boarder/boarder-detail"; // 현재 게시글 상세 페이지 뷰
  }

  @PostMapping("/reply/{replyId}/edit")
  public String updateReply(@PathVariable Long replyId,
                            @RequestParam String content,
                            @RequestParam Long boardId,
                            HttpSession session) {

    Member loginUser = (Member) session.getAttribute("loginUser");

    // 로그인 정보 없으면 mock 데이터 강제 세팅 (테스트용)
    if (loginUser == null) {
      loginUser = new Member();
      loginUser.setId(3L);       // 임의 아이디
      loginUser.setName("테스트유저");  // 임의 이름
      // 필요한 다른 필드도 세팅 가능
    }
    boardReplyService.updateReply(replyId, content, loginUser);

    return "redirect:/boarder/detail/" + boardId;  // 수정 후 상세 페이지로 이동
  }

  @PostMapping("/reply/{replyId}/delete")
  public String deleteReply(@PathVariable Long replyId, HttpSession session) {
    Member loginUser = (Member) session.getAttribute("loginUser");
    if (loginUser == null) {
      loginUser = new Member();
      loginUser.setId(3L);       // 임의 아이디
      loginUser.setName("테스트유저");  // 임의 이름
      // 필요한 다른 필드도 세팅 가능
    }
    boardReplyService.blindReply(replyId, loginUser);
    Long boardId = boardReplyService.findById(replyId).getBoard().getId();
    return "redirect:/boarder/detail/" + boardId;
  }






}