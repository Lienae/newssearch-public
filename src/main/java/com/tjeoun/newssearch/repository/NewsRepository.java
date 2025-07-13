package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface NewsRepository extends JpaRepository<News, Long> {

    // 카테고리별 최신 뉴스 2건
    List<News> findByCategoryAndIsBlindFalseOrderByPublishDateDesc(NewsCategory category, Pageable pageable);

    // 언론사별 최신 뉴스 2건
    List<News> findByMediaCompanyAndIsBlindFalseOrderByPublishDateDesc(NewsMediaCompany mediaCompany, Pageable pageable);
    List<News> findByMediaCompanyAndIsBlindFalseOrderByIdDesc(NewsMediaCompany mediaCompany, Pageable pageable);

    List<News> findByIsBlindFalseOrderByIdDesc();


    // 뉴스 URL 존재 여부 확인
    boolean existsByUrl(String url);


    Page<News> findByIsBlindFalse(Pageable pageable);
    Page<News> findByIsBlindFalseAndMediaCompanyOrderByIdDesc(NewsMediaCompany newsMediaCompany, Pageable pageable); // ✅ 올바른 방법


    Page<News> findAll(Pageable pageable);

    Page<News> findByCategory(NewsCategory category, Pageable pageable);

    Page<News> findByMediaCompany(NewsMediaCompany mediaCompany, Pageable pageable);

    Page<News> findByCategoryAndMediaCompany(NewsCategory category, NewsMediaCompany mediaCompany, Pageable pageable);

    Page<News> findByIsBlindFalseOrderByPublishDateDesc(Pageable pageable);


    List<News> findTop5ByOrderByPublishDateDesc();

    // 카테고리별 blind = false
    Page<News> findByCategoryAndIsBlindFalse(NewsCategory category, Pageable pageable);

    // 언론사별 blind = false
    Page<News> findByMediaCompanyAndIsBlindFalse(NewsMediaCompany mediaCompany, Pageable pageable);

    // 카테고리 + 언론사별 blind = false
    Page<News> findByCategoryAndMediaCompanyAndIsBlindFalse(NewsCategory category, NewsMediaCompany mediaCompany, Pageable pageable);

    // 특정 URL로 뉴스 조회
    Optional<News> findByUrl(String url);

    // 전체 뉴스 중 최신 2건
    List<News> findTop2ByIsBlindFalseOrderByPublishDateDesc();
}
