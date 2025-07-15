package com.tjeoun.newssearch;

import com.tjeoun.newssearch.config.MockMemberFactory;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class MemberTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MockMemberFactory mockMemberFactory;

    @Test
    @DisplayName("회원 가입 테스트")
    void userSignupTest() {
        // given
        Member member = mockMemberFactory.createUser("mockuser@gmail.com");

        // when
        Member saveMember = memberRepository.save(member);

        // then
        assertThatCode(() -> {
            Member loadMember = memberRepository.findByEmail("mockuser@gmail.com").orElseThrow();
            assertEquals(saveMember.getId(), loadMember.getId());
            assertEquals(saveMember.getEmail(), loadMember.getEmail());
            assertEquals(saveMember.getPassword(), loadMember.getPassword());
            assertEquals(saveMember.getName(), loadMember.getName());
            assertEquals(saveMember.getRole(), loadMember.getRole());

        }).doesNotThrowAnyException();

    }

}
