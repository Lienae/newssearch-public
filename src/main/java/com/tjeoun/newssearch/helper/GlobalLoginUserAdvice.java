package com.tjeoun.newssearch.helper;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalLoginUserAdvice {
  private final MemberRepository memberRepository;

  @ModelAttribute("loginUser")
  public Member loginUser(HttpSession session) {
    Member loginUser = (Member) session.getAttribute("loginUser");
    if (loginUser == null) {
      loginUser = memberRepository.findById(3L).orElse(null);  // 테스트 계정
      session.setAttribute("loginUser", loginUser);
    }
    System.out.println(">>> loginUser: " + loginUser);
    System.out.println(">>> loginUser ID: " + (loginUser != null ? loginUser.getId() : "null"));
    return loginUser;
  }
}
