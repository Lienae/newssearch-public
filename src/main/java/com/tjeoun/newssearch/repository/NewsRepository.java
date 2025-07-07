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
    boolean existsByUrl(String url);


    Page<News> findByIsBlindFalse(Pageable pageable);

    Page<News> findAll(Pageable pageable);

    Page<News> findByCategory(NewsCategory category, Pageable pageable);

    Page<News> findByMediaCompany(NewsMediaCompany mediaCompany, Pageable pageable);

    Page<News> findByCategoryAndMediaCompany(NewsCategory category, NewsMediaCompany mediaCompany, Pageable pageable);


    List<News> findTop5ByOrderByPublishDateDesc();
}
