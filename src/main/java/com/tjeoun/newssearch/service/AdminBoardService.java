package com.tjeoun.newssearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.repository.BoardRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminBoardService {

  private final BoardRepository boardRepository;

  public Page<AdminBoardDto> getBoards(int page, int size, String category) {
    Page<Board> boards;
    if ("ALL".equals(category)) {
      boards = boardRepository.findByIs_blindFalse(PageRequest.of(page, size));
    } else {
      boards = boardRepository.findByNewsCategoryAndIs_blindFalse(
        NewsCategory.valueOf(category), PageRequest.of(page, size));
    }
    return boards.map(AdminBoardDto::convertToDto);
  }

  public AdminBoardDto getBoardDto(Long id) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

    return AdminBoardDto.convertToDto(board);
  }

  @Transactional
  public void updateBoard(Long id, AdminBoardDto dto) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
    board.setTitle(dto.getTitle());
    board.setContent(dto.getContent());
  }

  @Transactional
  public void softDeleteBoard(Long id) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
    board.set_blind(true);
  }
}
