package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.document.BoardDocument;
import org.springframework.data.domain.Page; // Page 인터페이스 임포트
import org.springframework.data.domain.Pageable; // Pageable 인터페이스 임포트
import org.springframework.data.elasticsearch.annotations.Query; // @Query 어노테이션 임포트
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface BoardDocumentRepository extends ElasticsearchRepository<BoardDocument, String> {

  // 1. 제목만 검색하는 메서드를 Page<BoardDocument>를 반환하고 Pageable을 인자로 받도록 수정
  Page<BoardDocument> findByTitleContaining(String keyword, Pageable pageable);

  // 2. 내용만 검색하는 메서드를 Page<BoardDocument>를 반환하고 Pageable을 인자로 받도록 수정
  Page<BoardDocument> findByContentContaining(String keyword, Pageable pageable);

  // 3. 제목 또는 내용 중 하나라도 키워드를 포함하는 검색을 위한 메서드 추가 (가장 권장)
  //    이 메서드를 사용하면 BoardSearchService에서 if/else 없이 통합 검색이 가능합니다.
  @Query("{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"content\": \"?0\"}}]}}")
  Page<BoardDocument> searchByTitleOrContent(String keyword, Pageable pageable);
}