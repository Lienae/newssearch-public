package com.tjeoun.newssearch;

import com.tjeoun.newssearch.config.MockMemberFactory;
import com.tjeoun.newssearch.config.MockNewsFactory;
import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.dto.NewsReplyDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.entity.NewsReply;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.repository.NewsReplyRepository;
import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static java.time.LocalDate.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
public class NewsTest {
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private NewsReplyRepository newsReplyRepository;
    @Autowired
    private NewsDocumentRepository newsDocumentRepository;
    @Autowired
    private MockNewsFactory mockNewsFactory;
    @Autowired
    private MockMemberFactory mockMemberFactory;

    @Test
    @DisplayName("뉴스 등록 테스트")
    void newsSaveTest() {
        // given
        String url = "http://testUrl.kr";
        String title = "testTitle";
        String content = "testContent";
        String imageUrl = "c:/testurl.jpg";
        String author = "테스트";
        NewsCategory category = NewsCategory.SOCIAL;
        NewsMediaCompany company = NewsMediaCompany.YTN;
        NewsDto newsDto = NewsDto.builder()
                .url(url)
                .title(title)
                .content(content)
                .imageUrl(imageUrl)
                .author(author)
                .category(category)
                .mediaCompany(company)
                .publishDate(LocalDate.now())
                .build();
        News news = News.createNewsFromDto(newsDto);

        //when
        News savedNews = newsRepository.save(news);

        //then
        Assertions.assertThatCode(() -> {
            News loadedNews = newsRepository.findById(savedNews.getId()).orElseThrow();
            assertEquals(savedNews.getId(), loadedNews.getId());
            assertEquals(savedNews.getTitle(), loadedNews.getTitle());
            assertEquals(savedNews.getContent(), loadedNews.getContent());
            assertEquals(savedNews.getImageUrl(), loadedNews.getImageUrl());
            assertEquals(savedNews.getAuthor(), loadedNews.getAuthor());
            assertEquals(savedNews.getCategory(), loadedNews.getCategory());
            assertEquals(savedNews.getMediaCompany(), loadedNews.getMediaCompany());

        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("뉴스 댓글달기 테스트")
    void newsReplyTest() {
        // given
        News news = mockNewsFactory.createNews();
        Member user = mockMemberFactory.createUser("mockuser@test.com");
        NewsReply newsReply = NewsReply.createNewsReply(
                NewsReplyDto.builder()
                        .content("test")
                        .build()
        );
        newsReply.setMember(user);
        newsReply.setNews(news);
        // when
        NewsReply savedNewsReply = newsReplyRepository.save(newsReply);
        // then
        Assertions.assertThatCode(() -> {
            NewsReply loadedNewsReply = newsReplyRepository.findById(savedNewsReply.getId()).orElseThrow();
            assertEquals(savedNewsReply.getId(), loadedNewsReply.getId());
            assertEquals(savedNewsReply.getContent(), loadedNewsReply.getContent());
            assertEquals(savedNewsReply.getNews(), loadedNewsReply.getNews());
            assertEquals(savedNewsReply.getMember(), loadedNewsReply.getMember());

        }).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Elasticsearch 도큐먼트 저장 및 삭제 테스트")
    public void testSaveAndDeleteNewsDocument() {
        String testId = "test1";
        // 저장
        NewsDocument doc = new NewsDocument();
        doc.setId(testId);
        doc.setUrl("http://example.com/news1");
        doc.setTitle("JUnit Elasticsearch 테스트 뉴스");
        doc.setImageUrl("http://example.com/image.jpg");
        doc.setContent("Elasticsearch 테스트용 뉴스 내용입니다.");
        doc.setPublishDate(now());
        doc.setAuthor("테스터");
        doc.setCategory("POLITICS");
        doc.setMediaCompany("KOREA_NEWS");

        NewsDocument saved = newsDocumentRepository.save(doc);
        assertEquals(testId, saved.getId());

        // 삭제
        newsDocumentRepository.deleteById(testId);

        // 삭제 확인
        boolean exists = newsDocumentRepository.existsById(testId);
        assertFalse(exists);
    }
}
