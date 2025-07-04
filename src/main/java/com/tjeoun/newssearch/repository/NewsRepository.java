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

    @Query("SELECT n FROM News n WHERE n.is_blind = false")
    Page<News> findByIs_blindFalse(Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.category = :category AND n.is_blind = false")
    Page<News> findByCategoryAndIs_blindFalse(@Param("category") NewsCategory category, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.mediaCompany = :mediaCompany AND n.is_blind = false")
    Page<News> findByMediaCompanyAndIs_blindFalse(@Param("mediaCompany") NewsMediaCompany mediaCompany, Pageable pageable);

    @Query("SELECT n FROM News n WHERE n.category = :category AND n.mediaCompany = :mediaCompany AND n.is_blind = false")
    Page<News> findByCategoryAndMediaCompanyAndIs_blindFalse(@Param("category") NewsCategory newsCategory, @Param("mediaCompany") NewsMediaCompany mediaCompany, Pageable pageable);
    void deleteByTitle(String title);
    Optional<News> findByTitle(String title);

    List<News> findTop5ByOrderByPublishDateDesc();
}
