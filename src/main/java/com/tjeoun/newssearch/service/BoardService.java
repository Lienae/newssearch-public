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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private String uploadDir;
    private final NewsRepository newsRepository;


    private final BoardRepository boardRepository;
    private final AttachFileRepository attachFileRepository;
    private final BoardReplyService boardReplyService;
    private final BoardDocumentRepository boardDocumentRepository;
    private final AttachFileService attachFileService;

    // --- saveAttachFiles 메서드 수정 (예외 래핑) ---
    private void saveAttachFiles(List<MultipartFile> files, Board board) {
        if (files == null || files.isEmpty()) return;

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String uuid = UUID.randomUUID().toString();
            String serverFilename = uuid + "_" + originalFilename;

            try {
                Path uploadPath = Paths.get(uploadDir, serverFilename);
                // 디렉토리가 존재하지 않으면 생성합니다. 파일 경로가 아닌 디렉토리 경로를 확인해야 합니다.
                if (!Files.exists(uploadPath.getParent())) { // ★ .getParent() 추가
                    Files.createDirectories(uploadPath.getParent());
                }
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
                // ★ IOException을 RuntimeException으로 래핑하여 @Transactional이 롤백하도록 유도
                throw new RuntimeException("파일 업로드에 실패했습니다.", e);
            }
        }
    }

    @Transactional
    public void saveBoard(BoardDto boardDto, Member loginUser, String newsUrl) {
        Board board = null;
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

}
