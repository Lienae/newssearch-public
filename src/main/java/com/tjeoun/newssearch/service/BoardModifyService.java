package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.*;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardModifyService {
  private final BoardRepository boardRepository;
  private final AttachFileRepository attachFileRepository;
  private final BoardReplyRepository boardReplyRepository;
  private final MemberRepository memberRepository;
  private final BoardDocumentRepository boardDocumentRepository;

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

    // 엘라스틱서치 BoardDocument 업데이트 및 저장
    //    (ID는 String 타입이므로 Long -> String 변환)
    BoardDocument boardDocument = boardDocumentRepository.findById(String.valueOf(id))
      .orElseGet(BoardDocument::new); // 문서가 없을 경우 새로 생성 (보통은 존재해야 함)

    boardDocument.setId(String.valueOf(board.getId())); // 항상 ID는 설정
    boardDocument.setTitle(board.getTitle()); // DB에서 업데이트된 제목으로 설정
    boardDocument.setContent(board.getContent()); // DB에서 업데이트된 내용으로 설정
    boardDocument.setNewsCategory(board.getNewsCategory()); // DB에서 업데이트된 카테고리로 설정
    if (board.getCreatedDate() != null) {
      // BoardDocument의 @JsonFormat pattern과 동일하게 포맷터 생성
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
      boardDocument.setCreatedDate(board.getCreatedDate().format(formatter));
    } else {
      boardDocument.setCreatedDate(null); // 또는 적절한 기본값 설정
    }
    boardDocument.setBlind(board.isBlind()); // 숨김 여부도 동기화
    boardDocument.setAdminArticle(board.isAdminArticle()); // 관리자 게시글 여부 동기화

    boardDocumentRepository.save(boardDocument); // 엘라스틱서치 저장


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
    boardRepository.save(board);

    // 엘라스틱서치 BoardDocument 업데이트
    BoardDocument boardDocument = boardDocumentRepository.findById(String.valueOf(boardId))
      .orElseThrow(() -> new IllegalArgumentException("엘라스틱서치 문서를 찾을 수 없습니다."));
    boardDocument.setBlind(board.isBlind());
    boardDocumentRepository.save(boardDocument); // 엘라스틱서치 저장

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
