package com.tjeoun.newssearch;

import com.tjeoun.newssearch.config.MockBoardFactory;
import com.tjeoun.newssearch.config.MockMemberFactory;
import com.tjeoun.newssearch.config.MockNewsFactory;
import com.tjeoun.newssearch.dto.EmoticonDto;
import com.tjeoun.newssearch.entity.*;
import com.tjeoun.newssearch.enums.EmoticonEnum;
import com.tjeoun.newssearch.repository.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class EmoticonTest {
    @Autowired
    MockMemberFactory mockMemberFactory;

    @Autowired
    MockBoardFactory mockBoardFactory;

    @Autowired
    MockNewsFactory mockNewsFactory;

    Member member1;
    Member member2;
    Member admin;
    Board boardWithNews;
    Board boardWithoutNews;
    Board adminArticle;
    News news;

    @BeforeEach
    void setup() {
        member1 = mockMemberFactory.createUser("test1@test.com");
        member2 = mockMemberFactory.createUser("test2@test.com");
        admin = mockMemberFactory.createUser("admin@test.com");
        news = mockNewsFactory.createNews();
        boardWithNews = mockBoardFactory.createBoard(member1, news);
        boardWithoutNews = mockBoardFactory.createBoard(member2, null);
        adminArticle = mockBoardFactory.createAdmin(admin, news);
    }

    @Autowired
    EmoticonRepository emoticonRepository;

    @Autowired
    ViewEmoticonSummaryRepository viewEmoticonSummaryRepository;

    @Test
    @DisplayName("이모티콘 테스트")
    public void testEmoticon() {
        // given

        Emoticon emoticon = Emoticon.makeEmoticon(EmoticonDto.builder()
                .member(member1)
                .news(news)
                .emoticonEnum(EmoticonEnum.FUNNY)
                .build());

        // when
        Emoticon savedEmoticon = emoticonRepository.save(emoticon);

        // then
        assertThatCode(() -> {
            assertEquals(1, viewEmoticonSummaryRepository.count());
            viewEmoticonSummaryRepository.findAll().forEach(viewEmoticonSummary -> System.out.println(viewEmoticonSummary.toString()));

        }).doesNotThrowAnyException();
    }
}
