package com.tjeoun.newssearch;

import com.tjeoun.newssearch.dto.AttachFileDto;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.AttachFileRepository;
import com.tjeoun.newssearch.repository.BoardRepository;
import com.tjeoun.newssearch.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class AttachFileTest {
    @Autowired
    private AttachFileRepository attachFileRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Test
    @DisplayName("파일첨부 메타데이터 입력 테스트")
    public void testAttachFile() {
        // given
        Member member = Member.createMember(SignUpDto.builder()
                .email("test@test.com")
                .password("test")
                .name("test")
                .role(UserRole.USER)
                .build(), passwordEncoder);
        memberRepository.save(member);

        Board board = Board.createBoard(BoardDto.builder()
                .title("test")
                .content("test")
                .author(member)
                .password("test")
                .newsCategory(NewsCategory.CULTURE)
                .build(), passwordEncoder);
        boardRepository.save(board);

        AttachFileDto dto = AttachFileDto.builder()
                .fileName("TestFile")
                .fileSize(1L)
                .board(board)
                .build();
        AttachFile attachFile = AttachFile.createAttachFile(dto);

        // when
        AttachFile savedAttachFile = attachFileRepository.save(attachFile);

        // then
        assertThatCode(() -> {
            AttachFile loadedAttachFile = attachFileRepository.findById(attachFile.getId()).orElseThrow();
            assertEquals(savedAttachFile.getId(), loadedAttachFile.getId());

        }).doesNotThrowAnyException();
    }
}
