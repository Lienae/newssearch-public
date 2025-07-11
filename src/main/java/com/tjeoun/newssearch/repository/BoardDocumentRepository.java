package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.enums.NewsCategory;
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

  // 1. 카테고리 필터만 적용 (키워드 검색 없을 때)
  Page<BoardDocument> findByNewsCategory(NewsCategory newsCategory, Pageable pageable);
  // 1-1. 블라인드되지 않은 + 카테고리
  Page<BoardDocument> findByNewsCategoryAndIsBlindFalse(NewsCategory newsCategory, Pageable pageable);
  // 1-2. 관리자 글 + 카테고리
  Page<BoardDocument> findByNewsCategoryAndIsAdminArticle(NewsCategory newsCategory, boolean isAdminArticle, Pageable pageable);

  // 2. 블라인드되지 않은 글 (키워드/카테고리 없을 때)
  Page<BoardDocument> findByIsBlindFalse(Pageable pageable);

  // 3. 관리자 글 (키워드/카테고리 없을 때)
  Page<BoardDocument> findByIsAdminArticle(boolean isAdminArticle, Pageable pageable);


  // 4. 제목 검색 + 블라인드 여부
  Page<BoardDocument> findByTitleContainingAndIsBlindFalse(String keyword, Pageable pageable);
  // 5. 내용 검색 + 블라인드 여부
  Page<BoardDocument> findByContentContainingAndIsBlindFalse(String keyword, Pageable pageable);
  // 6. 제목/내용 검색 + 블라인드 여부
  @Query("{\"bool\": {\"must\": [" +
    "{\"term\": {\"isBlind\": false}}," +
    "{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"content\": \"?0\"}}]}}" +
    "]}}")
  Page<BoardDocument> searchByTitleOrContentAndIsBlindFalse(String keyword, Pageable pageable);


  // 7. 제목 검색 + 카테고리 + 블라인드 여부
  Page<BoardDocument> findByTitleContainingAndNewsCategoryAndIsBlindFalse(String keyword, NewsCategory newsCategory, Pageable pageable);
  // 8. 내용 검색 + 카테고리 + 블라인드 여부
  Page<BoardDocument> findByContentContainingAndNewsCategoryAndIsBlindFalse(String keyword, NewsCategory newsCategory, Pageable pageable);
  // 9. 제목/내용 검색 + 카테고리 + 블라인드 여부
  @Query("{\"bool\": {\"must\": [" +
    "{\"term\": {\"isBlind\": false}}," +
    "{\"term\": {\"newsCategory.keyword\": \"?1\"}}," +
    "{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"content\": \"?0\"}}]}}" +
    "]}}")
  Page<BoardDocument> searchByTitleOrContentAndNewsCategoryAndIsBlindFalse(String keyword, NewsCategory newsCategory, Pageable pageable);


  // 10. 제목 검색 + 카테고리 + 관리자 글 여부
  Page<BoardDocument> findByTitleContainingAndNewsCategoryAndIsAdminArticle(String keyword, NewsCategory newsCategory, boolean isAdminArticle, Pageable pageable);
  // 11. 내용 검색 + 카테고리 + 관리자 글 여부
  Page<BoardDocument> findByContentContainingAndNewsCategoryAndIsAdminArticle(String keyword, NewsCategory newsCategory, boolean isAdminArticle, Pageable pageable);
  // 12. 제목/내용 검색 + 카테고리 + 관리자 글 여부
  @Query("{\"bool\": {\"must\": [" +
    "{\"term\": {\"isAdminArticle\": ?2}}," + // ?2는 세 번째 파라미터 (isAdminArticle)
    "{\"term\": {\"newsCategory.keyword\": \"?1\"}}," + // ?1은 두 번째 파라미터 (newsCategory)
    "{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"content\": \"?0\"}}]}}" +
    "]}}")
  Page<BoardDocument> searchByTitleOrContentAndNewsCategoryAndIsAdminArticle(String keyword, NewsCategory newsCategory, boolean isAdminArticle, Pageable pageable);

  // 13. 제목 검색 + 관리자 글 여부
  Page<BoardDocument> findByTitleContainingAndIsAdminArticle(String keyword, boolean isAdminArticle, Pageable pageable);

  // 14. 내용 검색 + 관리자 글 여부
  Page<BoardDocument> findByContentContainingAndIsAdminArticle(String keyword, boolean isAdminArticle, Pageable pageable);

  // 15. 제목 또는 내용 검색 + 관리자 글 여부
  @Query("{\"bool\": {\"must\": [" +
    "{\"term\": {\"isAdminArticle\": ?1}}," + // ?1은 두 번째 파라미터 (isAdminArticle)
    "{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"content\": \"?0\"}}]}}" + // ?0은 첫 번째 파라미터 (keyword)
    "]}}")
  Page<BoardDocument> searchByTitleOrContentAndIsAdminArticle(String keyword, boolean isAdminArticle, Pageable pageable);
}