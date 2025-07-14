package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.enums.NewsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {


  @Query("SELECT b FROM Board b WHERE b.isBlind = false ORDER BY b.createdDate DESC")
  List<Board> findByIsBlindFalseOrderByCreatedDateDesc();

  @Query("SELECT b FROM Board b WHERE b.newsCategory = :category AND b.isBlind = false ORDER BY b.createdDate DESC")
  Page<Board> findByNewsCategoryAndIsBlindFalse(@Param("category") NewsCategory category, Pageable pageable);

  @Query("SELECT b FROM Board b WHERE b.isBlind = false ORDER BY b.createdDate DESC")
  Page<Board> findByIsBlindFalseOrderByCreatedDateDesc(Pageable pageable);

  // 개수 카운트 메서드 (명시적 쿼리)
  long countByIsBlind(Boolean isBlind);


  @Query("SELECT b FROM Board b WHERE b.isAdminArticle = true AND b.isBlind = false ORDER BY b.createdDate DESC")
  Page<Board> findAdminBoards(Pageable pageable);

  // editor's note 카테고리 분류
  @Query("SELECT b FROM Board b WHERE b.isAdminArticle = true AND b.newsCategory = :category AND b.isBlind = false ORDER BY b.createdDate DESC")
  Page<Board> findAdminBoardsByCategory(@Param("category") NewsCategory category, Pageable pageable);


  Page<Board> findAll(Pageable pageable);
  Page<Board> findByNewsCategory(NewsCategory category, Pageable pageable);


  List<Board> findTop5ByOrderByCreatedDateDesc();

  //  검색 기능
  // 관리자용 (isBlind 도 검색 가능하게)
  Page<Board> findByTitleContaining(String title, Pageable pageable);
  Page<Board> findByContentContaining(String content, Pageable pageable);
  Page<Board> findByTitleContainingAndNewsCategory(String title, NewsCategory category, Pageable pageable);
  Page<Board> findByContentContainingAndNewsCategory(String content, NewsCategory category, Pageable pageable);

  // 사용자용 (isBlind = false 포함)
  Page<Board> findByTitleContainingAndIsBlindFalse(String title, Pageable pageable);
  Page<Board> findByContentContainingAndIsBlindFalse(String content, Pageable pageable);
  Page<Board> findByTitleContainingAndNewsCategoryAndIsBlindFalse(String title, NewsCategory category, Pageable pageable);
  Page<Board> findByContentContainingAndNewsCategoryAndIsBlindFalse(String content, NewsCategory category, Pageable pageable);


}
