package com.tjeoun.newssearch.repository;

import com.tjeoun.newssearch.document.NewsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NewsDocumentRepository extends ElasticsearchRepository<NewsDocument, String> {
}
