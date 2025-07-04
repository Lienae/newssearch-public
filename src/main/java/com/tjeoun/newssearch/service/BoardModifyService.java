package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.AttachFileRepository;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardModifyService {
  private final BoardRepository boardRepository;
  private final AttachFileRepository attachFileRepository;
  private final BoardReplyRepository boardReplyRepository;

  private final String uploadDir = "C:/workspace/newssearch/images/upload";

  @Transactional
  public void updateBoard(BoardDto boardDto) {
    Board board = boardRepository.findById(boardDto.getId())
      .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + boardDto.getId()));

    board.setTitle(boardDto.getTitle());
    board.setContent(boardDto.getContent());
    board.setNewsCategory(boardDto.getNewsCategory());

    boardRepository.save(board);
  }
  @Transactional
  public void updateBoardAndFiles(Long id,
                                  BoardDto boardDto,
                                  MultipartFile[] files,
                                  List<Long> deleteFileIds,
                                  Member loginUser) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));

    if (!board.getAuthor().getId().equals(loginUser.getId())) {
      throw new IllegalArgumentException("수정 권한이 없습니다.");
    }

    board.setTitle(boardDto.getTitle());
    board.setContent(boardDto.getContent());
    board.setNewsCategory(boardDto.getNewsCategory());

    boardRepository.save(board);

    // 삭제 요청된 첨부파일 처리
    if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
      for (Long fileId : deleteFileIds) {
        attachFileRepository.findById(fileId).ifPresent(file -> {

          // 파일이 해당 게시글에 속한 것인지 확인
          if (!file.getBoard().getId().equals(id)) {
            throw new SecurityException("파일이 해당 게시글에 속하지 않습니다.");
          }

          // 실제 파일 삭제
          java.io.File realFile = new java.io.File(uploadDir, file.getServerFilename());
          if (realFile.exists()) {
            realFile.delete();
          }

          attachFileRepository.delete(file);
        });
      }
    }

    if (files != null) {
      for (MultipartFile file : files) {
        if (file.isEmpty()) continue;

        String serverFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path savePath = Paths.get(uploadDir).resolve(serverFilename);

        try {
          Files.copy(file.getInputStream(), savePath);
        } catch (IOException e) {
          throw new RuntimeException("파일 저장 실패", e);
        }

        AttachFile attachFile = AttachFile.builder()
          .board(board)
          .originalFilename(file.getOriginalFilename())
          .serverFilename(serverFilename)
          .size(file.getSize())
          .build();

        attachFileRepository.save(attachFile);
      }
    }
  }

  @Transactional
  public void blindBoard(Long boardId) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
    board.setIsBlind(true); // 게시글 숨김

    List<BoardReply> replies = boardReplyRepository.findByBoardId(boardId);
    for (BoardReply reply : replies) {
      reply.setIsBlind(true); // 댓글 숨김
    }
  }

}
