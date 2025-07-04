package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("SignUpDto", new SignUpDto());
        return "member/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute(name = "SignUpDto") SignUpDto dto) {
        dto.setRole(UserRole.USER);
        memberService.save(dto);
        return "redirect:/member/login";
    }
}
