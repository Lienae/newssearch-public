package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.enums.NewsCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
  @Query("SELECT b FROM Board b WHERE b.is_blind = false")
  Page<Board> findByIs_blindFalse(Pageable pageable);

  @Query("SELECT b FROM Board b WHERE b.newsCategory = :newsCategory AND b.is_blind = false")
  Page<Board> findByNewsCategoryAndIs_blindFalse(@Param("newsCategory") NewsCategory newsCategory, Pageable pageable);


  List<Board> findTop5ByOrderByCreatedDateDesc();
}
