package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttachFileRepository extends JpaRepository<AttachFile, Long> {
  List<AttachFile> findByBoard(Board board);
}
