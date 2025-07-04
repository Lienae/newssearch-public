package com.tjeoun.newssearch.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boarder")
public class BoardController {

  private final BoardService boardService;
  private final MemberRepository memberRepository;
  private final BoardReplyService boardReplyService;

  // 글쓰기
  @GetMapping("/write")
  public String boarderWrite(Model model) {
    model.addAttribute("boardDto", new BoardDto());
    model.addAttribute("categories", NewsCategory.values());

    return "boarder/boarder-write";
  }

  // 글 저장
  @PostMapping
  public String saveBoard(@ModelAttribute BoardDto boardDto) {

    // DB에서 실제 회원 조회
    Member member = memberRepository.findById(3L)
      .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));

    boardDto.setAuthor(member);  // DB에서 조회한 회원 객체 넣기

    // 관리자면 isAdminArticle true, 아니면 false
    boolean isAdmin = member.getRole() == UserRole.ADMIN;
    boardDto.setIsAdminArticle(isAdmin);

    // boardDto.password가 null이면 임시 비밀번호로 설정
    if (boardDto.getPassword() == null) {
      boardDto.setPassword("mockpw");
      System.out.println("DEBUG: password was null, set to mockpw");
    }
    if (boardDto.getNewsCategory() == null) {
      boardDto.setNewsCategory(NewsCategory.POLITICS);  // 원하는 기본값 입력
    }

    boardService.saveBoard(boardDto);
    return "redirect:/boarder/list";
  }

  // 글 목록 조회 (카테고리별)
  @GetMapping("/list")
  public String boarderList(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "15") int size,
                            @RequestParam(defaultValue = "ALL") String category,
                            @RequestParam(required = false) String filter) {

    // 게시글 총 개수 조회 (blind 처리 안된 글만)
    long totalVisibleBoards = boardService.countVisibleBoards();
    model.addAttribute("totalVisibleBoards", totalVisibleBoards);

    Page<Board> boardPage;

    // 관리자 필터 + 카테고리 분기
    if ("admin".equalsIgnoreCase(filter)) {
      if (!"ALL".equalsIgnoreCase(category)) {
        boardPage = boardService.getAdminBoardsByCategory(category, page, size);
      } else {
        boardPage = boardService.getAdminBoards(page, size);
      }
    } else {
      if (!"ALL".equalsIgnoreCase(category)) {
        boardPage = boardService.getBoardsByCategory(category, page, size);
      } else {
        boardPage = boardService.getBoards(page, size);
      }
    }

    model.addAttribute("boardPage", boardPage);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("category", category.toUpperCase());
    model.addAttribute("filter", filter);
    return "boarder/boarder-list";
  }



  // 게시글 상세 페이지
  @GetMapping("/detail/{id}")
  public String boarderDetail(@PathVariable Long id, Model model, HttpSession session) {
    Board board = boardService.getBoardById(id)
      .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
    List<BoardReply> replies = boardReplyService.findRepliesByBoardId(id); // 댓글 목록


    long replyCount = boardReplyService.countByBoardId(id); // 댓글 수 가져오기

    // 첨부파일 목록 조회 (서비스 메서드 하나로 통일)
    List<AttachFile> attachFiles = boardService.getAttachFilesByBoardId(id);



    model.addAttribute("board", board);
    model.addAttribute("replies", replies);
    model.addAttribute("editingReplies", List.of());
    model.addAttribute("attachFiles", attachFiles);
    model.addAttribute("replyCount", boardReplyService.countVisibleReplies(id));


    // 세션에서 로그인한 사용자 꺼내서 모델에 추가
    Member loginUser = (Member) session.getAttribute("loginUser");
    // 로그인 유저가 없으면 테스트용 임시 유저 넣기
    if (loginUser == null) {
      loginUser = memberRepository.findById(3L)
        .orElse(null);
    }
    model.addAttribute("loginUser", loginUser);




    return "boarder/boarder-detail";
  }



}