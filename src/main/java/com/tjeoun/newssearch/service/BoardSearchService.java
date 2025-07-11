package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.BoardDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardSearchService {

  private final BoardDocumentRepository boardDocumentRepository;

  // BoardService의 getFilteredBoards 로직을 여기에 통합
  public Page<Board> getFilteredAndSearchedBoards(String keyword, String searchType, Pageable pageable, String categoryStr, String filter, Member loginUser) {
    Page<BoardDocument> esBoardDocuments;
    NewsCategory newsCategory = null;
    boolean isLoginUserAdmin = loginUser.getRole() == UserRole.ADMIN;
    boolean isAdminFilter = "admin".equalsIgnoreCase(filter) && isLoginUserAdmin;
    boolean isAllCategory = "ALL".equalsIgnoreCase(categoryStr) || categoryStr == null;

    // 카테고리 ENUM 파싱
    if (!isAllCategory) {
      try {
        newsCategory = NewsCategory.valueOf(categoryStr.toUpperCase());
      } catch (IllegalArgumentException e) {
        log.warn("유효하지 않은 카테고리: {}", categoryStr);
        // 유효하지 않은 카테고리인 경우, 빈 페이지 반환
        return Page.empty(pageable);
      }
    }

    // --- 검색 로직 (키워드 유무에 따라 분기) ---
    if (keyword != null && !keyword.isBlank()) { // 키워드가 있는 경우 (검색 요청)
      if (isAdminFilter) { // 관리자 모드 + 관리자 계정 로그인
        if (isAllCategory) { // 모든 카테고리
          if ("title".equalsIgnoreCase(searchType)) {
            esBoardDocuments = boardDocumentRepository.findByTitleContainingAndIsAdminArticle(keyword, true, pageable);
          } else if ("content".equalsIgnoreCase(searchType)) {
            esBoardDocuments = boardDocumentRepository.findByContentContainingAndIsAdminArticle(keyword, true, pageable);
          } else { // 'all' (제목 또는 내용)
            esBoardDocuments = boardDocumentRepository.searchByTitleOrContentAndIsAdminArticle(keyword, true, pageable);
          }
        } else { // 특정 카테고리
          if ("title".equalsIgnoreCase(searchType)) {
            esBoardDocuments = boardDocumentRepository.findByTitleContainingAndNewsCategoryAndIsAdminArticle(keyword, newsCategory, true, pageable);
          } else if ("content".equalsIgnoreCase(searchType)) {
            esBoardDocuments = boardDocumentRepository.findByContentContainingAndNewsCategoryAndIsAdminArticle(keyword, newsCategory, true, pageable);
          } else { // 'all' (제목 또는 내용)
            esBoardDocuments = boardDocumentRepository.searchByTitleOrContentAndNewsCategoryAndIsAdminArticle(keyword, newsCategory, true, pageable);
          }
        }
      } else { // 일반 사용자 모드 (또는 관리자 모드지만 로그인한 사용자가 관리자가 아닌 경우)
        // 블라인드되지 않은 글만 검색
        if (isAllCategory) { // 모든 카테고리
          if ("title".equalsIgnoreCase(searchType)) {
            esBoardDocuments = boardDocumentRepository.findByTitleContainingAndIsBlindFalse(keyword, pageable);
          } else if ("content".equalsIgnoreCase(searchType)) {
            esBoardDocuments = boardDocumentRepository.findByContentContainingAndIsBlindFalse(keyword, pageable);
          } else { // 'all' (제목 또는 내용)
            esBoardDocuments = boardDocumentRepository.searchByTitleOrContentAndIsBlindFalse(keyword, pageable);
          }
        } else { // 특정 카테고리
          if ("title".equalsIgnoreCase(searchType)) {
            esBoardDocuments = boardDocumentRepository.findByTitleContainingAndNewsCategoryAndIsBlindFalse(keyword, newsCategory, pageable);
          } else if ("content".equalsIgnoreCase(searchType)) {
            esBoardDocuments = boardDocumentRepository.findByContentContainingAndNewsCategoryAndIsBlindFalse(keyword, newsCategory, pageable);
          } else { // 'all' (제목 또는 내용)
            esBoardDocuments = boardDocumentRepository.searchByTitleOrContentAndNewsCategoryAndIsBlindFalse(keyword, newsCategory, pageable);
          }
        }
      }
    } else { // 키워드가 없는 경우 (일반 목록 조회 또는 카테고리/필터링만)
      if (isAdminFilter) { // 관리자 모드 + 관리자 계정 로그인
        if (isAllCategory) {
          esBoardDocuments = boardDocumentRepository.findByIsAdminArticle(true, pageable); // 모든 관리자 글
        } else {
          esBoardDocuments = boardDocumentRepository.findByNewsCategoryAndIsAdminArticle(newsCategory, true, pageable); // 특정 카테고리 관리자 글
        }
      } else { // 일반 사용자 모드 (또는 관리자 모드지만 로그인한 사용자가 관리자가 아닌 경우)
        if (isAllCategory) {
          esBoardDocuments = boardDocumentRepository.findByIsBlindFalse(pageable); // 모든 블라인드되지 않은 글
        } else {
          esBoardDocuments = boardDocumentRepository.findByNewsCategoryAndIsBlindFalse(newsCategory, pageable); // 특정 카테고리, 블라인드되지 않은 글
        }
      }
    }
    // BoardDocument 리스트를 Board 엔티티 리스트로 변환
    List<Board> boardsFromEs = esBoardDocuments.getContent().stream().map(doc -> {
      Board board = new Board();
      board.setId(Long.valueOf(doc.getId()));
      board.setTitle(doc.getTitle());
      board.setContent(doc.getContent());

      Member author = new Member();
      author.setName(doc.getWriter());
      author.setId(0L); // DB에서 가져오지 않으면 임시 ID 또는 null 설정
      board.setAuthor(author);

      // createdDate (String -> LocalDateTime) 변환
      if (doc.getCreatedDate() != null && !doc.getCreatedDate().isEmpty()) {
        try {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
          board.setCreatedDate(LocalDateTime.parse(doc.getCreatedDate(), formatter));
        } catch (Exception e) {
          log.error("Elasticsearch createdDate '{}' 파싱 오류: {}", doc.getCreatedDate(), e.getMessage());
          board.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        }
      } else {
        board.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
      }

      board.setNewsCategory(doc.getNewsCategory());
      board.setAdminArticle(doc.isAdminArticle());
      board.setIsBlind(doc.isBlind());

      return board;
    }).collect(Collectors.toList());

    return new PageImpl<>(boardsFromEs, pageable, esBoardDocuments.getTotalElements());
  }
}