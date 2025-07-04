package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.repository.AttachFileRepository;
import com.tjeoun.newssearch.service.BoardModifyService;
import com.tjeoun.newssearch.service.BoardService;
import com.tjeoun.newssearch.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/boarder/edit")
public class BoardModifyController {
  private final BoardModifyService boardModifyService;
  private final MemberRepository memberRepository;
  private final BoardService boardService;
  private final AttachFileRepository attachFileRepository;

  /* ---------- 헬퍼 ---------- */
  /** 개발용: 세션에 loginUser가 없으면 테스트 계정(id=1L)을 넣어 반환 */
  private Member getLoginUser(HttpSession session) {
    Member loginUser = (Member) session.getAttribute("loginUser");
    if (loginUser == null) {
      loginUser = memberRepository.findById(1L).orElse(null);   // ★ 테스트 계정
      session.setAttribute("loginUser", loginUser);
    }
    return loginUser;
  }
  /* -------------------------- */

  // 수정 페이지 열기(GET)
  @GetMapping("/{id}")
  public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
    Board board = boardService.findById(id);

    Member loginUser = getLoginUser(session);
    if (loginUser == null || !loginUser.getId().equals(board.getAuthor().getId())) {
      throw new IllegalArgumentException("수정 권한이 없습니다.");
    }

    BoardDto boardDto = BoardDto.builder()
      .id(board.getId())
      .title(board.getTitle())
      .content(board.getContent())
      .author(board.getAuthor())
      .newsCategory(board.getNewsCategory())
      .build();

    // 첨부파일 목록 조회
    List<AttachFile> attachFiles = attachFileRepository.findByBoardId(id);

    model.addAttribute("boardDto", boardDto);
    model.addAttribute("categories", NewsCategory.values());
    model.addAttribute("attachFiles", attachFiles);

    return "boarder/boarder-modify"; // 팀원이 만든 복붙한 수정 페이지 뷰 파일명
  }

  // 수정 처리(POST)
  @PostMapping("/{id}")
  public String updateBoard(@PathVariable Long id,
                            @ModelAttribute BoardDto boardDto,
                            @RequestParam(value = "files", required = false) MultipartFile[] files, // 추가
                            @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
                            HttpSession session) {

    Board board = boardService.findById(id);
    Member loginUser = getLoginUser(session);  // 헬퍼 메서드 활용
    if (loginUser == null || !loginUser.getId().equals(board.getAuthor().getId())) {
      throw new IllegalArgumentException("수정 권한이 없습니다.");
    }

    boardDto.setId(id);
    boardDto.setAuthor(board.getAuthor()); // 기존 작성자 유지

    boardModifyService.updateBoardAndFiles(id, boardDto, files, deleteFileIds, loginUser);  // 파일 처리 포함 서비스 호출

    return "redirect:/boarder/detail/" + id;
  }


  // 게시글 숨기기 (is_blind 처리)
  @PostMapping("/delete/{id}")
  public String blindBoard(@PathVariable Long id, HttpSession session) {
    Board board = boardService.findById(id);

    Member loginUser = (Member) session.getAttribute("loginUser");
    if (loginUser == null || !loginUser.getId().equals(board.getAuthor().getId())) {
      throw new IllegalArgumentException("삭제 권한이 없습니다.");
    }

    boardModifyService.blindBoard(id);  // 삭제가 아닌 숨기기 처리로 변경
    return "redirect:/boarder/list";
  }

}