package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.BoardReplyDto;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardReplyService {
  private final BoardReplyRepository boardReplyRepository;
  private final BoardRepository boardRepository;

  // 댓글 저장
  @Transactional
  public void saveReply(BoardReplyDto dto) {
    Board board = boardRepository.findById(dto.getBoard().getId())
      .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

    dto.setBoard(board);  // board 객체 다시 셋팅
    BoardReply reply = BoardReply.createBoardReply(dto);
    boardReplyRepository.save(reply);
  }

  // 게시글 ID로 댓글 목록 조회
  public List<BoardReply> findRepliesByBoardId(Long boardId) {
    return boardReplyRepository.findVisibleRepliesByBoardId(boardId);
  }

  // 댓글 개수 반환
  public long countByBoardId(Long boardId) {
    return boardReplyRepository.countByBoardId(boardId);
  }

  // 댓글 수정 메서드
  public void updateReply(Long replyId, String newContent, Member loginUser) {
    BoardReply reply = boardReplyRepository.findById(replyId)
      .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + replyId));

    // 작성자와 로그인 사용자가 일치하는지 확인 (권한 체크)
    if (reply.getMember() == null || !reply.getMember().getId().equals(loginUser.getId())) {
      throw new IllegalStateException("댓글 작성자만 수정할 수 있습니다.");
    }

    reply.setContent(newContent);
    boardReplyRepository.save(reply);  // JPA는 변경 감지를 해도 되지만 명시적 저장도 무방
  }

  // 댓글 조회
  public BoardReply findById(Long replyId) {
    return boardReplyRepository.findById(replyId)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다. id=" + replyId));
  }

  // 댓글 삭제
  @Transactional
  public void blindReply(Long replyId, Member loginUser) {
    BoardReply reply = boardReplyRepository.findById(replyId)
      .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

    if (!reply.getMember().getId().equals(loginUser.getId())) {
      throw new IllegalStateException("작성자만 삭제할 수 있습니다.");
    }

    reply.setIsBlind(true);
    boardReplyRepository.save(reply);
  }
  public long countVisibleReplies(Long boardId) {
    return boardReplyRepository.countVisibleReplies(boardId);
  }





}
