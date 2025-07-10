package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.BoardReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BoardReplyRepository extends JpaRepository<BoardReply, Long> {
  List<BoardReply> findAllByBoardIdOrderByCreatedDateDesc(Long boardId);

  @Query("SELECT r FROM BoardReply r WHERE r.board.id = :boardId AND r.isBlind = false ORDER BY r.createdDate DESC")
  List<BoardReply> findVisibleRepliesByBoardId(@Param("boardId") Long boardId);

  List<BoardReply> findByBoardId(Long boardId);



}
