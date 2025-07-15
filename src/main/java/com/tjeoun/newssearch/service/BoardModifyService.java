package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardModifyService {
    private final BoardRepository boardRepository;
    private final AttachFileRepository attachFileRepository;
    private final BoardReplyRepository boardReplyRepository;
    private final MemberRepository memberRepository;
    private final BoardDocumentRepository boardDocumentRepository;
    private final AttachFileService attachFileService;



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

        // 엘라스틱서치 동기화
        BoardDocument boardDocument = boardDocumentRepository.findById(String.valueOf(id))
                .orElseGet(BoardDocument::new);

        boardDocument.setId(String.valueOf(board.getId()));
        boardDocument.setTitle(board.getTitle());
        boardDocument.setContent(board.getContent());
        boardDocument.setNewsCategory(board.getNewsCategory().name());
        boardDocument.setCreatedDate(board.getCreatedDate().atOffset(ZoneOffset.ofHours(9)).withNano(0));
        boardDocument.setBlind(board.getIsBlind());
        boardDocument.setAdminArticle(board.isAdminArticle());

        boardDocumentRepository.save(boardDocument);

        // 첨부파일 삭제 처리
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            for (Long fileId : deleteFileIds) {
                attachFileRepository.findById(fileId).ifPresent(file -> {
                    if (!file.getBoard().getId().equals(id)) {
                        throw new SecurityException("파일이 해당 게시글에 속하지 않습니다.");
                    }
                    // 로컬 파일 삭제
                    File realFile = new File(attachFileService.getUploadDir(), file.getServerFilename()); // ※ getUploadDir() 메서드가 필요
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

                try {
                    String serverFilename = attachFileService.saveFile(
                            file.getOriginalFilename(),
                            file.getBytes()
                    );

                    AttachFile attachFile = AttachFile.builder()
                            .board(board)
                            .originalFilename(file.getOriginalFilename())
                            .serverFilename(serverFilename)
                            .size(file.getSize())
                            .build();

                    attachFileRepository.save(attachFile);
                } catch (IOException e) {
                    throw new RuntimeException("파일 저장 실패", e);
                }
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
        boardDocument.setBlind(board.getIsBlind());
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
