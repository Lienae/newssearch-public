package com.tjeoun.newssearch.service;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
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
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
                log.info("categoryStr = {}", categoryStr);
                newsCategory = NewsCategory.valueOf(categoryStr.toUpperCase());
                log.info("newsCategory = {}", newsCategory);

            } catch (IllegalArgumentException e) {
                log.warn("유효하지 않은 카테고리: {}", categoryStr);
                // 유효하지 않은 카테고리인 경우, 빈 페이지 반환
                return Page.empty(pageable);
            }
        }
        if (keyword != null && !keyword.isBlank()) {
            if (isAdminFilter) { // 관리자 모드 + 관리자 계정 로그인
                if (isAllCategory) { // 모든 카테고리
                    if ("title".equalsIgnoreCase(searchType)) {
                        esBoardDocuments = boardDocumentRepository.findByTitleContainingAndIsAdminArticle(keyword, true, pageable);
                    } else if ("content".equalsIgnoreCase(searchType)) {
                        esBoardDocuments = boardDocumentRepository.findByContentContainingAndIsAdminArticle(keyword, true, pageable);
                    } else { // 'all' (제목 또는 내용)
                        esBoardDocuments = boardDocumentRepository.searchByTitleOrContentAndIsAdminArticleAndIsBlindFalse(keyword, true, pageable);
                    }
                } else { // 특정 카테고리
                    if ("title".equalsIgnoreCase(searchType)) {
                        esBoardDocuments = boardDocumentRepository.findByTitleContainingAndNewsCategoryAndIsAdminArticle(keyword, newsCategory.name(), true, pageable);
                    } else if ("content".equalsIgnoreCase(searchType)) {
                        esBoardDocuments = boardDocumentRepository.findByContentContainingAndNewsCategoryAndIsAdminArticle(keyword, newsCategory.name(), true, pageable);
                    } else { // 'all' (제목 또는 내용)
                        esBoardDocuments = boardDocumentRepository.searchByTitleOrContentAndNewsCategoryAndIsAdminArticleAndIsBlindFalse(keyword, newsCategory.name(), true, pageable);
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
                        esBoardDocuments = boardDocumentRepository.findByTitleContainingAndNewsCategoryAndIsBlindFalse(keyword, newsCategory.name(), pageable);
                    } else if ("content".equalsIgnoreCase(searchType)) {
                        esBoardDocuments = boardDocumentRepository.findByContentContainingAndNewsCategoryAndIsBlindFalse(keyword, newsCategory.name(), pageable);
                    } else { // 'all' (제목 또는 내용)
                        esBoardDocuments = boardDocumentRepository.searchByTitleOrContentAndNewsCategoryAndIsBlindFalse(keyword, newsCategory.name(), pageable);
                    }
                }
            }

        } else { // 키워드가 없는 경우 (일반 목록 조회 또는 카테고리/필터링만)
            if (isAdminFilter) { // 관리자 모드 + 관리자 계정 로그인
                if (isAllCategory) {
                    esBoardDocuments = boardDocumentRepository.findByIsAdminArticleAndIsBlindFalse(true, pageable); // 모든 관리자 글
                } else {
                    esBoardDocuments = boardDocumentRepository.findByNewsCategoryAndIsAdminArticleAndIsBlindFalse(newsCategory.name(), true, pageable); // 특정 카테고리 관리자 글
                }

            } else { // 일반 사용자 모드 (또는 관리자 모드지만 로그인한 사용자가 관리자가 아닌 경우)
                if (isAllCategory) {
                    esBoardDocuments = boardDocumentRepository.findByIsBlindFalse(pageable); // 모든 블라인드되지 않은 글
                } else {
                    esBoardDocuments = boardDocumentRepository.findByNewsCategoryAndIsBlindFalse(newsCategory.name(), pageable); // 특정 카테고리, 블라인드되지 않은 글
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
            author.setName(doc.getEmail());
            author.setId(0L); // DB에서 가져오지 않으면 임시 ID 또는 null 설정
            board.setAuthor(author);
            OffsetDateTime createdDate = Optional.ofNullable(doc.getCreatedDate()).orElse(OffsetDateTime.now().withNano(0));
            board.setCreatedDate(createdDate.toLocalDateTime());

            board.setNewsCategory(NewsCategory.valueOf(doc.getNewsCategory()));
            board.setAdminArticle(doc.isAdminArticle());
            board.setIsBlind(doc.isBlind());

            return board;
        }).collect(Collectors.toList());

        return new PageImpl<>(boardsFromEs, pageable, esBoardDocuments.getTotalElements());
    }
}