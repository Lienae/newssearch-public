package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.config.MockMemberFactory;
import com.tjeoun.newssearch.config.principal.PrincipalDetailsService;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    private Long testMemberId;
    @Autowired
    private MockMemberFactory mockMemberFactory;
    @Autowired
    private PrincipalDetailsService principalDetailsService;
    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("1234")
                .role(UserRole.ADMIN)
                .build());
        testMemberId = member.getId();

        UserDetails admin = principalDetailsService.loadUserByUsername("elixirel.chrome@gmail.com");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(admin, admin.getPassword(), admin.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);


    }

    @Test

    void testListPage() throws Exception {
        mockMvc.perform(get("/admin/members/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-list"))
                .andExpect(model().attributeExists("members"));
    }

    @Test

    void testEditForm() throws Exception {
        mockMvc.perform(get("/admin/members/edit")
                        .param("id", testMemberId.toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-edit"))
                .andExpect(model().attributeExists("member"));
    }

    @Test
    void testEditProcess() throws Exception {
        // given
        Member member = mockMemberFactory.createUser("mockuser@gmail.com");
        memberRepository.save(member);
        // when
        mockMvc.perform(post("/admin/members/edit/" + member.getId())
                        .with(csrf())
                        .param("name", "홍길동수정")
                        .param("email", "hong@test.com")
                        .param("role", "USER")
                        .param("password", "1234"))  // 비밀번호도 같이 보내는 형태
        // then
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/members/list*"));

        Member updated = memberRepository.findById(member.getId()).orElseThrow();
        assertEquals("홍길동수정", updated.getName());
        assertEquals(UserRole.USER, updated.getRole());
    }

    @Test
    void testDeleteProcess() throws Exception {
        // given
        Member member = mockMemberFactory.createUser("mockuser@gmail.com");
        memberRepository.save(member);

        // when
        mockMvc.perform(post("/admin/members/delete/" + member.getId())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/admin/members/list*"));

        // then
        assertDoesNotThrow(() -> {
            Member updated = memberRepository.findById(member.getId()).orElseThrow();
            assertTrue(updated.isBlind());
        });

    }
}

