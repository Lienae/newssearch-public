package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.AttachFileDto;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.repository.AttachFileRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

  private  static final String UPLOAD_DIR = "C:/workspace/newssearch/images/upload";

  private final BoardRepository boardRepository;
  private final AttachFileRepository attachFileRepository;

  private void saveAttachFiles(List<MultipartFile> files, Board board) {
    if (files == null || files.isEmpty()) return;

    for (MultipartFile file : files) {
      if (file.isEmpty()) continue;

      String originalFilename = file.getOriginalFilename();
      long size = file.getSize();
      String uuid = UUID.randomUUID().toString();
      String serverFilename = uuid + "_" + originalFilename;

      try {
        Path uploadPath = Paths.get(UPLOAD_DIR, serverFilename);
        file.transferTo(uploadPath.toFile());

        AttachFile attachFile = AttachFile.builder()
          .board(board)
          .size(size)
          .originalFilename(originalFilename)
          .serverFilename(serverFilename)
          .build();

        attachFileRepository.save(attachFile);

      } catch (IOException e) {
        log.error("파일 업로드 중 오류 발생: {}", e.getMessage(), e);
        throw new RuntimeException("파일 업로드에 실패했습니다.");
      }
    }
  }




  @Transactional
  public void saveBoard(BoardDto boardDto) {
    Board board = Board.createBoard(boardDto);
    List<MultipartFile> files = boardDto.getFiles();

    // Board 저장 (id 생성됨)
    boardRepository.save(board);

    // 첨부파일 저장 처리
    saveAttachFiles(files, board);

  }

  public List<AttachFile> getAttachFilesByBoardId(Long boardId) {
    return attachFileRepository.findByBoardId(boardId);
  }

  public Board findById(Long id) {
    return boardRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
  }


  // boardId로 첨부파일 리스트 조회
  public List<AttachFile> findAttachFilesByBoardId(Long boardId) {
    return attachFileRepository.findAllByBoardId(boardId);
  }

  // 숨김 처리된 게시글 제외 + 최신순 조회
  public List<Board> getAllBoards() {
    return boardRepository.findByIsBlindFalseOrderByCreatedDateDesc();
  }
    // 게시글 보기
    public Optional<Board> getBoardById(Long id) {
      return boardRepository.findById(id);
    }

  // 페이징 + 숨김 제외 + 최신순
  public Page<Board> getBoards(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return boardRepository.findByIsBlindFalseOrderByCreatedDateDesc(pageable);
  }


  //카테고리 + 숨김 제외 + 페이징
  public Page<Board> getBoardsByCategory(String newsCategoryStr, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    try {
      NewsCategory newsCategory = NewsCategory.valueOf(newsCategoryStr.toUpperCase());
      return boardRepository.findByNewsCategoryAndIsBlindFalse(newsCategory, pageable);
    } catch (IllegalArgumentException e) {
      return Page.empty(pageable);
    }
  }

  // 게시글 개수
  // BoardService.java
  public long countVisibleBoards() {
    return boardRepository.countVisibleBoards();
  }

  // 관리자 글 조회
  public Page<Board> getAdminBoards(int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return boardRepository.findAdminBoards(pageable);
  }

  public Page<Board> getAdminBoardsByCategory(String category, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
    NewsCategory newsCategory = NewsCategory.valueOf(category.toUpperCase());
    return boardRepository.findAdminBoardsByCategory(newsCategory, pageable);
  }







}
