package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}
