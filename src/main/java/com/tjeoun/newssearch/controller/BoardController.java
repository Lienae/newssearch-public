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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

  private final BoardService boardService;
  private final MemberRepository memberRepository;
  private final BoardReplyService boardReplyService;

  // 글쓰기
  @GetMapping("/write")
  public String boardWrite(@RequestParam(value = "newsUrl", required = false) String newsUrl,
                           Model model) {

    model.addAttribute("boardDto", new BoardDto());

    if (newsUrl != null && !newsUrl.isEmpty()) {
      var news = boardService.findNewsByUrl(newsUrl); // 뉴스 정보 가져오기

      if (news != null) {
        model.addAttribute("newsTitle", news.getTitle());
        model.addAttribute("newsCategory", news.getCategory());
        model.addAttribute("newsUrl", news.getUrl());
      }
    }

    model.addAttribute("categories", NewsCategory.values());
    return "board/board-write";
  }


  // 글 저장
  @PostMapping
  public String saveBoard(@ModelAttribute BoardDto boardDto,
                          @RequestParam(value = "newsUrl", required = false) String newsUrl,
                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
    Member loginUser = principalDetails.getMember();
    boardService.saveBoard(boardDto, loginUser, newsUrl);
    if (boardDto.getFiles() != null) {
      log.info("첨부파일 개수: {}", boardDto.getFiles().size());
      boardDto.getFiles().forEach(file ->
              log.info("파일 이름: {}, 크기: {}", file.getOriginalFilename(), file.getSize()));
    } else {
      log.info("첨부파일 리스트가 null 입니다.");
    }


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