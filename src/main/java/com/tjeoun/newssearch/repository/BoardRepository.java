package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}
