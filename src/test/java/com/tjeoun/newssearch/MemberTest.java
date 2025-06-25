package com.tjeoun.newssearch;

import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.UserRole;
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
public class MemberTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 테스트")
    void userSignupTest() {
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
        Member member = Member.createMenber(dto, passwordEncoder);

        // when
        Member saveMember = memberRepository.save(member);

        // then
        assertThatCode(() -> {
            Member loadMember = memberRepository.findById(1L).orElseThrow();
            assertEquals(saveMember.getId(), loadMember.getId());
            assertEquals(saveMember.getEmail(), loadMember.getEmail());
            assertEquals(saveMember.getPassword(), loadMember.getPassword());
            assertEquals(saveMember.getName(), loadMember.getName());
            assertEquals(saveMember.getRole(), loadMember.getRole());

        }).doesNotThrowAnyException();

    }

}
