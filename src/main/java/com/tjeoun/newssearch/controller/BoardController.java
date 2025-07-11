package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.config.principal.PrincipalDetails;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.BoardRepository;
import com.tjeoun.newssearch.repository.MemberRepository;
import com.tjeoun.newssearch.service.BoardReplyService;
import com.tjeoun.newssearch.service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;
  private final MemberRepository memberRepository;
  private final BoardReplyService boardReplyService;

  // 글쓰기
  @GetMapping("/write")
  public String boardWrite(Model model) {
    model.addAttribute("boardDto", new BoardDto());
    model.addAttribute("categories", NewsCategory.values());
    return "board/board-write";
  }

  // 글 저장
  @PostMapping
  public String saveBoard(@ModelAttribute BoardDto boardDto,
                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
    Member loginUser = principalDetails.getMember();
    boardService.saveBoard(boardDto, loginUser);
    return "redirect:/board/search";
  }



  // 게시글 상세 페이지
  @GetMapping("/detail/{id}")
  public String boardDetail(@PathVariable Long id,
                            Model model,
                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
    Member loginUser = principalDetails.getMember();
    model.addAllAttributes(boardService.getBoardDetail(id, loginUser));

    List<BoardReply> replies = boardReplyService.findRepliesByBoardId(id);
    model.addAttribute("replies", replies);

    return "board/board-detail";
  }




}