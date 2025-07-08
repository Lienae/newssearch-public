package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    // 카테고리별 최신 뉴스 2건
    List<News> findByCategoryAndBlindFalseOrderByPublishDateDesc(NewsCategory category, Pageable pageable);

    // 언론사별 최신 뉴스 2건
    List<News> findByMediaCompanyAndBlindFalseOrderByPublishDateDesc(NewsMediaCompany mediaCompany, Pageable pageable);

    // 뉴스 URL 존재 여부 확인
    boolean existsByUrl(String url);

    // 전체 뉴스 중 blind = false인 것만 페이지 조회
    Page<News> findByBlindFalse(Pageable pageable);

    // 카테고리별 blind = false
    Page<News> findByCategoryAndBlindFalse(NewsCategory category, Pageable pageable);

    // 언론사별 blind = false
    Page<News> findByMediaCompanyAndBlindFalse(NewsMediaCompany mediaCompany, Pageable pageable);

    // 카테고리 + 언론사별 blind = false
    Page<News> findByCategoryAndMediaCompanyAndBlindFalse(NewsCategory category, NewsMediaCompany mediaCompany, Pageable pageable);

    // 특정 URL로 뉴스 조회
    Optional<News> findByUrl(String url);

    // 전체 뉴스 중 최신 2건
    List<News> findTop2ByBlindFalseOrderByPublishDateDesc();
}
