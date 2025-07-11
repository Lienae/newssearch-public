package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.AttachFileRepository;
import com.tjeoun.newssearch.repository.BoardDocumentRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
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
    @Value("${upload.dir}")
    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    private final BoardRepository boardRepository;
    private final AttachFileRepository attachFileRepository;
    private final BoardReplyService boardReplyService;
    private final BoardDocumentRepository boardDocumentRepository;

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

    // --- saveBoard 메서드 수정 (try-catch 및 롤백 로직 추가) ---
    @Transactional // DB 트랜잭션 관리 어노테이션
    public void saveBoard(BoardDto boardDto, Member loginUser) {
        // board와 boardDocument를 try 블록 밖에서 선언하여 catch 블록에서 접근 가능하도록 합니다.
        Board board = null; // 초기화
        BoardDocument boardDocument = null; // 초기화

        try {
            // 작성자 설정
            boardDto.setAuthor(loginUser);

            // 관리자 여부 설정
            boolean isAdmin = loginUser.getRole() == UserRole.ADMIN;
            boardDto.setIsAdminArticle(isAdmin);

            // 카테고리가 null이면 기본값 설정
            if (boardDto.getNewsCategory() == null) {
                boardDto.setNewsCategory(NewsCategory.MISC);
            }

            // BoardDto → Board entity 생성
            board = Board.createBoard(boardDto); // board 객체 생성

            // DB에 Board 저장 (id 생성) - 이 시점에서 DB 트랜잭션 시작
            boardRepository.save(board);
            log.info("DB에 저장 완료: {}", board.getTitle());

            // Elasticsearch에 저장 (BoardDocument 생성 및 색인)
            boardDocument = Board.toDocument(board); // boardDocument 객체 생성
            log.info("엘라스틱에 저장 직전: {}", board.getTitle());
            log.info("엘라스틱에 저장될 createdDate: {}", boardDocument.getCreatedDate());
            boardDocumentRepository.save(boardDocument); // Elasticsearch 저장 시도
            log.info("엘라스틱에 저장 완료");

            // 첨부파일 처리
            List<MultipartFile> files = boardDto.getFiles();
            saveAttachFiles(files, board); // 이 메서드 내에서 예외 발생 시, @Transactional 롤백 발생

        } catch (Exception e) {
            // 게시글 저장, 엘라스틱서치 색인, 파일 업로드 중 어떤 단계에서든 예외 발생 시 이 블록 실행

            log.error("게시글 생성 및 색인 중 오류 발생: {}", e.getMessage(), e);

            // 엘라스틱서치 롤백 시도:
            // 만약 boardDocument가 생성되었고, 엘라스틱서치에 저장 시도 후 (부분적으로라도) 성공했으나
            // 이후 단계(예: 첨부파일 저장)에서 실패하여 현재 트랜잭션이 롤백되어야 할 경우,
            // 엘라스틱서치에 남아있는 해당 문서를 삭제하여 일관성을 유지합니다.
            if (boardDocument != null && boardDocument.getId() != null) {
                try {
                    boardDocumentRepository.deleteById(boardDocument.getId());
                    log.warn("오류 발생으로 인해 엘라스틱서치 문서 롤백 완료: ID={}", boardDocument.getId());
                } catch (Exception esDeleteException) {
                    // 엘라스틱서치 문서 삭제 중 오류가 발생했음을 로깅 (수동 처리 필요 가능성)
                    log.error("엘라스틱서치 문서 롤백 중 추가 오류 발생: ID={}, Error: {}", boardDocument.getId(), esDeleteException.getMessage());
                }
            }

            // DB 트랜잭션은 @Transactional 어노테이션에 의해 자동으로 롤백됩니다.
            // 따라서 boardRepository.delete()와 같은 명시적인 DB 롤백 코드는 필요 없습니다.

            // 발생한 예외를 다시 던져서 상위 호출자에게 알리고, 스프링의 @Transactional 롤백을 확정시킵니다.
            throw new RuntimeException("게시글 저장 중 예상치 못한 오류가 발생했습니다.", e);
        }
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

    /*

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

    // 게시글 개수 ( 사용자 )
    public long countByIsBlind(Boolean isBlind) {
        return boardRepository.countByIsBlind(isBlind);
    }
    // 게시글 개수 ( 관리자 )
    public long countAllBoards() {
        return boardRepository.count();
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


    // 검색 기능
    public Page<Board> getFilteredBoards(String categoryStr, String filter, String searchType, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        NewsCategory category = null;

        if (!"ALL".equalsIgnoreCase(categoryStr)) {
            try {
                category = NewsCategory.valueOf(categoryStr.toUpperCase());
            } catch (Exception ignored) {}
        }

        boolean isAdmin = "admin".equalsIgnoreCase(filter);

        // 키워드 검색
        if (keyword != null && !keyword.isBlank()) {
            if ("title".equals(searchType)) {
                if (isAdmin) {
                    return (category != null) ?
                      boardRepository.findByTitleContainingAndNewsCategory(keyword, category, pageable) :
                      boardRepository.findByTitleContaining(keyword, pageable);
                } else {
                    return (category != null) ?
                      boardRepository.findByTitleContainingAndNewsCategoryAndIsBlindFalse(keyword, category, pageable) :
                      boardRepository.findByTitleContainingAndIsBlindFalse(keyword, pageable);
                }
            } else if ("content".equals(searchType)) {
                if (isAdmin) {
                    return (category != null) ?
                      boardRepository.findByContentContainingAndNewsCategory(keyword, category, pageable) :
                      boardRepository.findByContentContaining(keyword, pageable);
                } else {
                    return (category != null) ?
                      boardRepository.findByContentContainingAndNewsCategoryAndIsBlindFalse(keyword, category, pageable) :
                      boardRepository.findByContentContainingAndIsBlindFalse(keyword, pageable);
                }
            }
        }

        // 키워드 없을 경우 (기존 목록)
        if (isAdmin) {
            return (category != null) ?
              boardRepository.findByNewsCategory(category, pageable) :
              boardRepository.findAll(pageable);
        } else {
            return (category != null) ?
              boardRepository.findByNewsCategoryAndIsBlindFalse(category, pageable) :
              boardRepository.findByIsBlindFalseOrderByCreatedDateDesc(pageable);
        }
    }
*/









}
