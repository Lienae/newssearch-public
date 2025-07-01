package com.tjeoun.newssearch.helper;

import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NewsSaveHelper {
    private final NewsRepository newsRepository;
    private final NewsDocumentRepository newsDocumentRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Void> saveArticle(Map<String, String> article) {
        try {
            News news = News.createNewsFromMap(article);
            newsRepository.save(news);

            NewsDocument newsDocument = new NewsDocument();
            newsDocumentRepository.save(newsDocument);
        } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new RuntimeException("Elasticsearch save failed. Rolled back RDB", e);
            }
        return CompletableFuture.completedFuture(null);
    }
}
