package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.entity.Board; // Board 엔티티 임포트
import com.tjeoun.newssearch.entity.Member; // Member 엔티티 임포트
import com.tjeoun.newssearch.enums.NewsCategory; // NewsCategory 임포트 (Board 변환 시 필요)
import com.tjeoun.newssearch.repository.BoardDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page; // Page 임포트
import org.springframework.data.domain.PageImpl; // PageImpl 임포트
import org.springframework.data.domain.Pageable; // Pageable 임포트
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // LocalDateTime 임포트 (날짜 변환 시 필요)
import java.time.format.DateTimeFormatter; // DateTimeFormatter 임포트 (날짜 변환 시 필요)
import java.time.temporal.ChronoUnit; // ChronoUnit 임포트 (날짜 변환 시 필요)
import java.util.List;
import java.util.stream.Collectors; // Collectors 임포트


@Slf4j
@Service
@RequiredArgsConstructor // Lombok이 생성자를 자동으로 만들어주므로 수동 생성자 필요 없음
public class BoardSearchService {

  private final BoardDocumentRepository boardDocumentRepository;

  // 기존 수동 생성자 제거: @RequiredArgsConstructor가 이미 모든 final 필드에 대한 생성자를 만들어 줍니다.
  // public BoardSearchService(BoardDocumentRepository boardDocumentRepository) {
  //   this.boardDocumentRepository = boardDocumentRepository;
  // }

  // List<BoardDocument> 대신 Page<Board>를 반환하도록 변경
  // 검색 타입(searchType)을 인자로 받아, 제목/내용 검색을 선택적으로 수행하도록 개선
  public Page<Board> searchBoards(String keyword, String searchType, Pageable pageable) {
    Page<BoardDocument> esBoardDocuments;

    // 검색 타입에 따라 엘라스틱서치 쿼리 분기
    if ("title".equalsIgnoreCase(searchType)) {
      esBoardDocuments = boardDocumentRepository.findByTitleContaining(keyword, pageable);
    } else if ("content".equalsIgnoreCase(searchType)) {
      esBoardDocuments = boardDocumentRepository.findByContentContaining(keyword, pageable);
    } else { // searchType이 없거나 title, content 외의 값일 경우 (기본값 또는 통합 검색)
      // BoardDocumentRepository에 searchByTitleOrContent 메서드가 정의되어 있어야 합니다.
      esBoardDocuments = boardDocumentRepository.searchByTitleOrContent(keyword, pageable);
    }


    // BoardDocument 리스트를 Board 엔티티 리스트로 변환
    List<Board> boardsFromEs = esBoardDocuments.getContent().stream().map(doc -> {
      Board board = new Board();
      board.setId(Long.valueOf(doc.getId()));
      board.setTitle(doc.getTitle());
      board.setContent(doc.getContent());

      Member author = new Member();
      author.setName(doc.getWriter()); // BoardDocument의 writer 이름을 그대로 사용
      author.setId(0L); // 검색 결과에서는 실제 ID가 필요 없으므로 임시 ID 또는 null 설정
      // (Long 타입이라 0L을 쓰는 게 안전합니다)
      board.setAuthor(author);

      // createdDate (String -> LocalDateTime) 변환
      if (doc.getCreatedDate() != null && !doc.getCreatedDate().isEmpty()) {
        try {
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
          board.setCreatedDate(LocalDateTime.parse(doc.getCreatedDate(), formatter));
        } catch (Exception e) {
          log.error("Elasticsearch createdDate '{}' 파싱 오류: {}", doc.getCreatedDate(), e.getMessage());
          board.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)); // 오류 시 현재 시간으로 대체
        }
      } else {
        board.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
      }

      // isBlind, isAdminArticle, newsCategory 등 BoardDocument에 없는 필드는
      // 필요하다면 BoardDocument에 추가하거나, 해당 board.getId()로 DB에서 Board를 다시 조회하여 채워야 합니다.
      // 현재는 검색 시 필수 정보가 아니라고 가정하고 임시 값 설정 (DB 조회는 추가적인 성능 오버헤드 발생)
      board.setIsBlind(false); // 검색 결과는 블라인드 처리 안된 것으로 가정
      board.setAdminArticle(false); // 검색 결과는 관리자 글이 아닌 것으로 가정
      board.setNewsCategory(NewsCategory.MISC); // 적절한 기본값 설정

      return board;
    }).collect(Collectors.toList());

    // Page<BoardDocument>의 페이징 정보를 그대로 사용하여 Page<Board> 객체 생성 및 반환
    return new PageImpl<>(boardsFromEs, pageable, esBoardDocuments.getTotalElements());
  }
}