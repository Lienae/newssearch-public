package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public String register(@ModelAttribute(name = "SignUpDto") SignUpDto dto, Model model) {
        dto.setRole(UserRole.USER);
        try {
            memberService.save(dto);
        } catch (IllegalStateException e) {
            model.addAttribute("errormsg", e.getMessage());
            return "member/register";
        }
        return "redirect:/member/login";
    }

    @GetMapping("/mypage")
    public String mypage(Model model, Principal principal) {
        model.addAttribute("member", memberService.findByEmail(principal.getName()));
        return "member/mypage";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute(name = "member") SignUpDto dto) {
        memberService.update(dto);
        return "redirect:/";
    }
}
