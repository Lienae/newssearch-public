package com.tjeoun.newssearch;

import com.tjeoun.newssearch.config.MockBoardFactory;
import com.tjeoun.newssearch.config.MockMemberFactory;
import com.tjeoun.newssearch.config.MockNewsFactory;
import com.tjeoun.newssearch.config.MockReplyFactory;
import com.tjeoun.newssearch.entity.*;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
import com.tjeoun.newssearch.repository.NewsReplyRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BoardTest {
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private BoardReplyRepository boardReplyRepository;
    @Autowired
    private MockMemberFactory mockMemberFactory;
    @Autowired
    private MockNewsFactory mockNewsFactory;
    @Autowired
    private MockBoardFactory mockBoardFactory;
    @Autowired
    private MockReplyFactory mockReplyFactory;
    @Autowired
    private NewsReplyRepository newsReplyRepository;

    @Test
    @DisplayName("게시글 저장 테스트")
    void boardSaveTest() {
        // given

        Member user = mockMemberFactory.createUser("mockuser1@gmail.com");
        Member admin = mockMemberFactory.createAdmin("mockadmin1@gmail.com");
        News news = mockNewsFactory.createNews();

        Board board = mockBoardFactory.createBoard(user, news);
        Board adminBoard = mockBoardFactory.createAdmin(admin, news);

        // when
        Board saveBoard = boardRepository.save(board);
        boardRepository.save(adminBoard);
        // then
        assertThatCode(() -> {
            Board loadBoard = boardRepository.findById(saveBoard.getId()).orElseThrow();
            assertEquals(saveBoard.getTitle(), loadBoard.getTitle());
            assertEquals(saveBoard.getContent(), loadBoard.getContent());
            assertEquals(saveBoard.getAuthor(), loadBoard.getAuthor());

        }).doesNotThrowAnyException();
    }
    @Test
    @DisplayName("댓글 달기 테스트")
    void boardReplyTest() {
        // given
        News news = mockNewsFactory.createNews();
        Member user = mockMemberFactory.createUser("mockuser1@gmail.com");
        Board board = mockBoardFactory.createBoard(user, news);
        BoardReply boardReply = mockReplyFactory.createBoardReply(board, user, false);
        NewsReply newsReply = mockReplyFactory.createNewsReply(news, user, false);

        // when
        BoardReply saveBoardReply = boardReplyRepository.save(boardReply);
        NewsReply saveNewsReply = newsReplyRepository.save(newsReply);

        // then
        assertThatCode(() -> {
            BoardReply loadBoardReply = boardReplyRepository.findById(saveBoardReply.getId()).orElseThrow();
            assertEquals(saveBoardReply.getContent(), loadBoardReply.getContent());
            assertEquals(saveBoardReply.getBoard(), loadBoardReply.getBoard());
            assertEquals(saveBoardReply.getMember(), loadBoardReply.getMember());

        }).doesNotThrowAnyException();

        assertThatCode(() -> {
            NewsReply loadNewsReply = newsReplyRepository.findById(saveNewsReply.getId()).orElseThrow();
            assertEquals(saveNewsReply.getContent(), loadNewsReply.getContent());
            assertEquals(saveNewsReply.getNews(), loadNewsReply.getNews());
            assertEquals(saveNewsReply.getMember(), loadNewsReply.getMember());

        }).doesNotThrowAnyException();

    }
}
