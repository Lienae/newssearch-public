package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.AdminAttachFileDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.repository.AttachFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.repository.BoardRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminBoardService {

  private final BoardRepository boardRepository;
  private final AttachFileService attachFileService;
  private final AttachFileRepository attachFileRepository;

  public Page<AdminBoardDto> getBoards(int page, int size, String category) {
    Page<Board> boards;
    if ("ALL".equals(category)) {
      boards = boardRepository.findByIs_blindFalse(PageRequest.of(page, size));
    } else {
      boards = boardRepository.findByNewsCategoryAndIs_blindFalse(
        NewsCategory.valueOf(category), PageRequest.of(page, size));
    }
    return boards.map(AdminBoardDto::fromEntity);
  }

  public AdminBoardDto getBoardDto(Long id) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

    return AdminBoardDto.fromEntity(board);
  }

  @Transactional
  public void softDeleteBoard(Long id) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
    board.set_blind(true);
  }

  @Transactional
  public void updateBoardWithFiles(Long id, AdminBoardDto dto, List<MultipartFile> files) throws Exception {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

    // 게시글 제목, 내용 업데이트
    board.setTitle(dto.getTitle());
    board.setContent(dto.getContent());

    // 기존 첨부 파일 삭제
    List<AttachFile> existingFiles = attachFileRepository.findByBoard(board);
    for (AttachFile existingFile : existingFiles) {
      attachFileService.deleteFile(existingFile.getServerFilename());
      attachFileRepository.delete(existingFile);
    }

    // 새 파일 업로드
    if (files != null && !files.isEmpty()) {
      for (MultipartFile file : files) {
        if (!file.isEmpty()) {
          String serverFilename = attachFileService.saveFile(file.getOriginalFilename(), file.getBytes());

          AttachFile attachFile = AttachFile.builder()
            .board(board)
            .size(file.getSize())
            .originalFilename(file.getOriginalFilename())
            .serverFilename(serverFilename)
            .build();

          attachFileRepository.save(attachFile);
        }
      }
    }
  }

  @Transactional(readOnly = true)
  public List<AdminAttachFileDto> getAttachFiles(Long boardId) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다"));

    List<AttachFile> fileEntities = attachFileRepository.findByBoard(board);

    return fileEntities.stream()
      .map(AdminAttachFileDto::fromEntity)
      .toList();
  }

  public List<AdminBoardDto> getRecentBoardList() {
    return boardRepository.findTop5ByOrderByCreatedDateDesc()
      .stream()
      .map(AdminBoardDto::fromEntity)
      .toList();
  }
}
