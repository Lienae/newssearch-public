package com.tjeoun.newssearch.helper;

import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsSaveHelper {
    private final NewsRepository newsRepository;
    private final NewsDocumentRepository newsDocumentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveArticle(Map<String, String> article) {
        News news = News.createNewsFromMap(article);
        newsRepository.save(news);
        NewsDocument newsDocument = new NewsDocument();
        newsDocumentRepository.save(newsDocument);
    }
}
