package com.tjeoun.newssearch.config;

import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MockBoardFactory {


    @Autowired
    BoardRepository boardRepository;

    public Board createBoard(Member member, News news) {
        if(news != null)
            return boardRepository.save(
                Board.builder()
                        .author(member)
                        .news(news)
                        .content("test")
                        .title("test")
                        .newsCategory(NewsCategory.SOCIAL)
                        .isBlind(false)
                        .build()
            );
        else
            return boardRepository.save(
                    Board.builder()
                            .author(member)
                            .content("test")
                            .title("test")
                            .newsCategory(NewsCategory.MISC)
                            .isBlind(false)
                            .build()
            );
    }

    public Board createAdmin(Member member, News news) {
        return boardRepository.save(
                Board.builder()
                        .author(member)
                        .news(news)
                        .content("test")
                        .title("test")
                        .newsCategory(NewsCategory.SOCIAL)
                        .isAdminArticle(true)
                        .isBlind(false)
                        .build()
        );
    }
}
