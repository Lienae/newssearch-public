package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.entity.NewsReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsReplyRepository extends JpaRepository<NewsReply, Long> {
    List<NewsReply> findByNewsAndIsBlindFalse(News news);
}
