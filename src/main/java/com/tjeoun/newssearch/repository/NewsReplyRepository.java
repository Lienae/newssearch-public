package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.entity.NewsReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsReplyRepository extends JpaRepository<NewsReply, Long> {
}
