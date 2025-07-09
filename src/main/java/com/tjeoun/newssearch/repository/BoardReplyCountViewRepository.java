package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.BoardReplyCountView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReplyCountViewRepository extends JpaRepository<BoardReplyCountView, Long> {
  BoardReplyCountView findByBoardId(Long boardId);
}
