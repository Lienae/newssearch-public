package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.AttachFileRepository;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
import com.tjeoun.newssearch.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardModifyService {
  private final BoardRepository boardRepository;
  private final AttachFileRepository attachFileRepository;
  private final BoardReplyRepository boardReplyRepository;
  private final MemberRepository memberRepository;

  private String uploadDir;
  @Value("${upload.dir}")
  public void setUploadDir(String uploadDir) {
    this.uploadDir = uploadDir;
  }


  public BoardDto getEditableBoard(Long boardId, Principal principal) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + boardId));

    Member loginUser = getMemberFromPrincipal(principal);

    if (!board.getAuthor().getId().equals(loginUser.getId())) {
      throw new IllegalArgumentException("수정 권한이 없습니다.");
    }

    return BoardDto.builder()
      .id(board.getId())
      .title(board.getTitle())
      .content(board.getContent())
      .author(board.getAuthor())
      .newsCategory(board.getNewsCategory())
      .build();
  }

  public List<AttachFile> listAttachmentsByBoardId(Long boardId) {
    return attachFileRepository.findByBoardId(boardId);
  }

  @Transactional
  public void editBoardWithAttachments(Long id,
                                       BoardDto boardDto,
                                       MultipartFile[] files,
                                       List<Long> deleteFileIds,
                                       Principal principal) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다. id=" + id));

    Member loginUser = getMemberFromPrincipal(principal);

    if (!board.getAuthor().getId().equals(loginUser.getId())) {
      throw new IllegalArgumentException("수정 권한이 없습니다.");
    }

    board.setTitle(boardDto.getTitle());
    board.setContent(boardDto.getContent());
    board.setNewsCategory(boardDto.getNewsCategory());

    boardRepository.save(board);

    // 첨부파일 삭제 처리
    if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
      for (Long fileId : deleteFileIds) {
        attachFileRepository.findById(fileId).ifPresent(file -> {
          if (!file.getBoard().getId().equals(id)) {
            throw new SecurityException("파일이 해당 게시글에 속하지 않습니다.");
          }
          File realFile = new java.io.File(uploadDir, file.getServerFilename());
          if (realFile.exists()) {
            realFile.delete();
          }
          attachFileRepository.delete(file);
        });
      }
    }

    // 첨부파일 추가 처리
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
  public void blindBoard(Long boardId, Principal principal) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    Member loginUser = getMemberFromPrincipal(principal);

    if (!board.getAuthor().getId().equals(loginUser.getId())) {
      throw new IllegalArgumentException("삭제 권한이 없습니다.");
    }

    board.setIsBlind(true);

    List<BoardReply> replies = boardReplyRepository.findByBoardId(boardId);
    for (BoardReply reply : replies) {
      reply.setIsBlind(true);
    }
  }

  private Member getMemberFromPrincipal(Principal principal) {
    String email = principal.getName();
    return memberRepository.findByEmail(email)
      .orElseThrow(() -> new IllegalArgumentException("로그인 사용자를 찾을 수 없습니다."));
  }
}
