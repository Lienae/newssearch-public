package com.tjeoun.newssearch.helper;

import com.tjeoun.newssearch.config.principal.PrincipalDetails;
import com.tjeoun.newssearch.entity.Member;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class LoginUserHelper {
  @ModelAttribute("loginUser")
  public Member addLoginUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
    return (principalDetails != null) ? principalDetails.getMember() : null;
  }
}
