package com.tjeoun.newssearch.config;

import com.tjeoun.newssearch.entity.*;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.NewsReplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockReplyFactory {


    @Autowired
    BoardReplyRepository boardReplyRepository;
    @Autowired
    NewsReplyRepository newsReplyRepository;

    public NewsReply createNewsReply(News news, Member member, boolean isBlind) {
        return NewsReply.builder()
                .news(news)
                .content("test")
                .member(member)
                .isBlind(isBlind)
                .build();
    }

    public BoardReply createBoardReply(Board board, Member member, boolean isBlind) {
        return BoardReply.builder()
                .board(board)
                .content("test")
                .member(member)
                .isBlind(isBlind)
                .build();
    }
}
