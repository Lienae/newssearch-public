package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.config.principal.PrincipalDetails;
import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.service.BoardSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardSearchController {
  private final BoardSearchService boardSearchService;

  @GetMapping({"/list", "/search"}) // 기존 /list 엔드포인트를 이쪽으로 통합
  public String search(
    @RequestParam(value = "keyword", required = false) String keyword, // keyword를 필수가 아님으로 변경
    @RequestParam(value = "searchType", defaultValue = "all") String searchType,
    @RequestParam(value = "page", defaultValue = "0") int page,
    @RequestParam(value = "size", defaultValue = "10") int size, // 한 페이지당 보여줄 게시글 수
    @RequestParam(defaultValue = "ALL") String category, // category 파라미터 추가
    @RequestParam(required = false) String filter, // filter 파라미터 추가
    @AuthenticationPrincipal PrincipalDetails principalDetails, // 로그인 유저 정보 받기
    Model model) {

    // 로그인 사용자 정보 가져오기 (getDefaultMember는 BoardSearchService로 옮겨야 합니다)
    Member loginUser = principalDetails.getMember();

    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

    // 모든 경우에 BoardSearchService 호출
    Page<Board> boardPage = boardSearchService.getFilteredAndSearchedBoards(keyword, searchType, pageable, category, filter, loginUser);

    long totalVisibleBoards = boardPage.getTotalElements(); // 엘라스틱서치에서 받은 Page 객체의 총 개수 사용

    model.addAttribute("boardPage", boardPage);
    model.addAttribute("currentPage", page);
    model.addAttribute("pageSize", size);
    model.addAttribute("category", category.toUpperCase());
    model.addAttribute("filter", filter);
    model.addAttribute("searchType", searchType);
    model.addAttribute("keyword", keyword);
    model.addAttribute("loginUser", loginUser);
    model.addAttribute("totalVisibleBoards", totalVisibleBoards);

    return "board/board-list";
  }
}
