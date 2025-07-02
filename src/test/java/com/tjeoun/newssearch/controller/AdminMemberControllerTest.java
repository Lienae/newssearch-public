package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

  @BeforeEach
  void setUp() {
    Member member = memberRepository.save(Member.builder()
      .name("홍길동")
      .email("hong@test.com")
      .password("1234")
      .role(UserRole.ADMIN)
      .build());
    testMemberId = member.getId();
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
    mockMvc.perform(post("/admin/members/edit/" + testMemberId)
        .with(csrf())
        .param("id", testMemberId.toString())
        .param("name", "홍길동수정")
        .param("email", "hong@test.com")
        .param("role", "USER")
        .param("password", "1234"))  // 비밀번호도 같이 보내는 형태
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/admin/members/list"));

    Member updated = memberRepository.findById(testMemberId).orElseThrow();
    assertEquals("홍길동수정", updated.getName());
    assertEquals(UserRole.USER, updated.getRole());
  }

  @Test
  void testDeleteProcess() throws Exception {
    mockMvc.perform(post("/admin/members/delete/" + testMemberId)
        .with(csrf()))
      .andExpect(status().is3xxRedirection())
      .andExpect(redirectedUrl("/admin/members/list"));

    assertFalse(memberRepository.findById(testMemberId).isPresent());
  }
}

