package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.BoardReplyDto;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.BoardReplyCountView;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.BoardReplyCountViewRepository;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardReplyService {
  private final BoardReplyRepository boardReplyRepository;
  private final BoardRepository boardRepository;
  private final BoardReplyCountViewRepository boardReplyCountViewRepository;

  // 댓글 저장
  @Transactional
  public void saveReply(Long boardId, String content, Member loginUser) {
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

    if (content == null || content.trim().isEmpty())
      throw new IllegalArgumentException("댓글 내용을 입력하세요.");

    BoardReply reply = BoardReply.builder()
      .content(content)
      .member(loginUser)
      .board(board)
      .isBlind(false)
      .build();

    boardReplyRepository.save(reply);
  }
  // 댓글 수정 페이지를 위한 데이터 셋팅
  public void prepareEditReplyPage(Long replyId, Long boardId, Member loginUser, Model model) {
    List<BoardReply> replies = findRepliesByBoardId(boardId);
    Board board = boardRepository.findById(boardId)
      .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

    model.addAttribute("editingReplies", List.of(replyId));
    model.addAttribute("replies", replies);
    model.addAttribute("board", board);
    model.addAttribute("loginUser", loginUser);

    // 기존 countVisibleReplies 대신 뷰 기반 조회 사용
    long replyCount = getReplyCountByBoardId(boardId);
    model.addAttribute("replyCount", replyCount);
  }

  // 게시글 ID로 댓글 목록 조회
  public List<BoardReply> findRepliesByBoardId(Long boardId) {
    return boardReplyRepository.findVisibleRepliesByBoardId(boardId);
  }


  // 댓글 수정
  @Transactional
  public void updateReply(Long replyId, String newContent, Member loginUser) {
    BoardReply reply = boardReplyRepository.findById(replyId)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));

    if (!reply.getMember().getId().equals(loginUser.getId())) {
      throw new IllegalStateException("댓글 작성자만 수정할 수 있습니다.");
    }

    reply.setContent(newContent);
  }

  // 댓글 삭제 (숨김)
  @Transactional
  public Long deleteReply(Long replyId, Member loginUser) {
    BoardReply reply = boardReplyRepository.findById(replyId)
      .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다."));

    if (!reply.getMember().getId().equals(loginUser.getId())) {
      throw new IllegalStateException("댓글 작성자만 삭제할 수 있습니다.");
    }

    reply.setIsBlind(true);
    return reply.getBoard().getId(); // 삭제 후 리디렉션용
  }



  public long getReplyCountByBoardId(Long boardId) {
    BoardReplyCountView view = boardReplyCountViewRepository.findByBoardId(boardId);
    return view != null ? view.getReplyCount() : 0;
  }




}
