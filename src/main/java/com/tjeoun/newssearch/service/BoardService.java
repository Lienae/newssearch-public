package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.*;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.AttachFileRepository;
import com.tjeoun.newssearch.repository.BoardDocumentRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {


    private final NewsRepository newsRepository;

    private final BoardRepository boardRepository;
    private final AttachFileRepository attachFileRepository;
    private final BoardReplyService boardReplyService;
    private final BoardDocumentRepository boardDocumentRepository;
    private final AttachFileService attachFileService;

    @Transactional
    public void saveBoard(BoardDto boardDto, Member loginUser, String newsUrl) {
        Board board;
        BoardDocument boardDocument = null;

        try {
            boardDto.setAuthor(loginUser);
            boolean isAdmin = loginUser.getRole() == UserRole.ADMIN;
            boardDto.setIsAdminArticle(isAdmin);

            News news = null;
            if (newsUrl != null && !newsUrl.isEmpty()) {
                news = newsRepository.findByUrl(newsUrl).orElse(null);
            }

            if (boardDto.getNewsCategory() == null) {
                boardDto.setNewsCategory(NewsCategory.MISC);
            }

            board = Board.builder()
                    .id(boardDto.getId())
                    .title(boardDto.getTitle())
                    .content(boardDto.getContent())
                    .author(boardDto.getAuthor())
                    .newsCategory(boardDto.getNewsCategory())
                    .news(news)
                    .isAdminArticle(boardDto.getIsAdminArticle() != null ? boardDto.getIsAdminArticle() : false)
                    .isBlind(false)
                    .build();

            boardRepository.save(board);

            boardDocument = Board.toDocument(board);
            boardDocumentRepository.save(boardDocument);

            List<MultipartFile> files = boardDto.getFiles();
            if (files != null && !files.isEmpty()) {
                for (MultipartFile file : files) {
                    if (file.isEmpty()) continue;

                    String serverFilename = attachFileService.saveFile(
                            file.getOriginalFilename(),
                            file.getBytes()
                    );

                    AttachFile attachFile = AttachFile.builder()
                            .board(board)
                            .size(file.getSize())
                            .originalFilename(file.getOriginalFilename())
                            .serverFilename(serverFilename)
                            .build();

                    attachFileRepository.save(attachFile);
                }
            }


        } catch (Exception e) {
            log.error("게시글 생성 및 색인 중 오류 발생: {}", e.getMessage(), e);
            if (boardDocument != null && boardDocument.getId() != null) {
                try {
                    boardDocumentRepository.deleteById(boardDocument.getId());
                    log.warn("엘라스틱서치 문서 롤백 완료: ID={}", boardDocument.getId());
                } catch (Exception esEx) {
                    log.error("엘라스틱서치 문서 롤백 중 오류 발생: {}", esEx.getMessage());
                }
            }
            throw new RuntimeException("게시글 저장 중 오류가 발생했습니다.", e);
        }
    }

    public News findNewsByUrl(String newsUrl) {
        return newsRepository.findByUrl(newsUrl).orElse(null);
    }

    public Map<String, Object> getBoardDetail(Long id, Member loginUser) {
        Map<String, Object> result = new HashMap<>();

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        List<BoardReply> replies = boardReplyService.findRepliesByBoardId(id);
        List<AttachFile> attachFiles = attachFileRepository.findByBoardId(id);


        result.put("board", board);
        result.put("replies", replies);
        result.put("editingReplies", List.of());  // 템플릿에서 쓰던 거 유지
        result.put("attachFiles", attachFiles);
        result.put("loginUser", loginUser);

        return result;
    }

    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id=" + id));
    }

}
