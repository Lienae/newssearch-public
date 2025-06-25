package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.BoardReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardReplyRepository extends JpaRepository<BoardReply, Long> {
}
