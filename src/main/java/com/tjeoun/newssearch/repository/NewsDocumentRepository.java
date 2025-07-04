package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.document.NewsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Optional;

public interface NewsDocumentRepository extends ElasticsearchRepository<NewsDocument, String> {
    void deleteByTitle(String title);
    Optional<NewsDocument> findByTitle(String title);
}
