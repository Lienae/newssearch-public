package com.tjeoun.newssearch;

import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.dto.BoardReplyDto;
import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
import com.tjeoun.newssearch.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("게시글 저장 테스트")
    void boardSaveTest() {
        // given
        String email = "testemail@test.com";
        String password = "testpassword";
        String name = "테스트";
        UserRole role = UserRole.USER;
        SignUpDto dto = SignUpDto.builder()
                .email(email)
                .password(password)
                .name(name)
                .role(role)
                .build();
        Member member = Member.createMember(dto, passwordEncoder);
        Member saveMember = memberRepository.save(member);

        String title = "testTitle";
        String content = "testContent";
        BoardDto boardDto = BoardDto.builder()
                .title(title)
                .content(content)
                .author(saveMember)
                .password(password)
                .newsCategory(NewsCategory.POLITICS)
                .build();
        Board board = Board.createBoard(boardDto, passwordEncoder);
        BoardDto adminBoardDto = BoardDto.builder()
                .title(title)
                .content(content)
                .author(saveMember)
                .password(password)
                .newsCategory(NewsCategory.POLITICS)
                .isAdminArticle(true)
                .build();
        Board adminBoard = Board.createBoard(adminBoardDto, passwordEncoder);

        // when
        Board saveBoard = boardRepository.save(board);
        Board saveAdminBoard = boardRepository.save(adminBoard);
        // then
        assertThatCode(() -> {
            Board loadBoard = boardRepository.findById(saveBoard.getId()).orElseThrow();
            assertEquals(saveBoard.getTitle(), loadBoard.getTitle());
            assertEquals(saveBoard.getContent(), loadBoard.getContent());
            assertEquals(saveBoard.getAuthor(), loadBoard.getAuthor());
            assertFalse(saveBoard.is_admin_article());
            assertTrue(saveAdminBoard.is_admin_article());
        }).doesNotThrowAnyException();
    }
    @Test
    @DisplayName("댓글 달기 테스트")
    void boardReplyTest() {
        // given
        String email = "testemail@test.com";
        String password = "testpassword";
        String name = "테스트";
        UserRole role = UserRole.USER;
        SignUpDto dto = SignUpDto.builder()

                .email(email)
                .password(password)
                .name(name)
                .role(role)
                .build();
        Member member = Member.createMember(dto, passwordEncoder);
        Member saveMember = memberRepository.save(member);

        String title = "testTitle";
        String content = "testContent";
        BoardDto boardDto = BoardDto.builder()
                .title(title)
                .content(content)
                .author(saveMember)
                .password(password)
                .newsCategory(NewsCategory.POLITICS)
                .build();
        Board board = Board.createBoard(boardDto, passwordEncoder);
        Board saveBoard = boardRepository.save(board);

        BoardReplyDto boardReplyDto = BoardReplyDto.builder()
                .content(content)
                .board(saveBoard)
                .member(saveMember)
                .password(password)
                .build();
        BoardReply boardReply = BoardReply.createBoardReply(boardReplyDto);

        // when
        BoardReply saveBoardReply = boardReplyRepository.save(boardReply);

        // then
        assertThatCode(() -> {
            BoardReply loadBoardReply = boardReplyRepository.findById(saveBoardReply.getId()).orElseThrow();
            assertEquals(saveBoardReply.getContent(), loadBoardReply.getContent());
            assertEquals(saveBoardReply.getBoard(), loadBoardReply.getBoard());
            assertEquals(saveBoardReply.getMember(), loadBoardReply.getMember());
            assertEquals(saveBoardReply.getBoard(), loadBoardReply.getBoard());

        }).doesNotThrowAnyException();

    }
}
