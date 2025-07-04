package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {
    boolean existsByUrl(String url);
    void deleteByTitle(String title);
    Optional<News> findByTitle(String title);
}
