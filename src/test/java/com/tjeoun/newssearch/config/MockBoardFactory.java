package com.tjeoun.newssearch.config;

import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.BoardRepository;
import com.tjeoun.newssearch.repository.MemberRepository;
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
                        .build()
            );
        else
            return boardRepository.save(
                    Board.builder()
                            .author(member)
                            .content("test")
                            .title("test")
                            .newsCategory(NewsCategory.MISC)
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
                        .is_admin_article(true)
                        .build()
        );
    }
}
