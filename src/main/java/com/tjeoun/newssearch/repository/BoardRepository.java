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

    Page<Board> findAll(Pageable pageable);
  Page<Board> findByNewsCategory(NewsCategory category, Pageable pageable);


  List<Board> findTop5ByOrderByCreatedDateDesc();

}
