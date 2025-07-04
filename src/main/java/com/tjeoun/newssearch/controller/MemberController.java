package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.service.MemberService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @GetMapping("/findpassword")
    public String findpassword() {
        return "member/find-password";
    }
    @PostMapping("/findpassword")
    public String findpassword(@RequestParam String email, Model model) {
        try {
            memberService.sendEmailForPasswordReset(email);
            return "member/find-password-sent";
        } catch (MessagingException e) {
            model.addAttribute("errormsg", "메일 전송에 실패했습니다. 다시 시도해 주세요.");
            return "member/find-password";
        } catch (UsernameNotFoundException e) {
            model.addAttribute("errormsg", e.getMessage());
            return "member/find-password";
        }
    }
    @GetMapping("/resetpassword")
    public String resetpassword(@RequestParam String token, Model model) {
        try {
            Member member = memberService.getMemberFromToken(token);
            model.addAttribute("SignUpDto", SignUpDto.builder()
                            .id(member.getId())
                            .build());
            model.addAttribute("token", token);
            return "member/reset-password";
        } catch (IllegalStateException e) {
            model.addAttribute("errormsg", e.getMessage());
            return "member/find-password";
        }
    }

    @PostMapping("/resetpassword")
    public String resetPassword(@ModelAttribute(name = "member") SignUpDto dto,
                                @RequestParam String token,
                                Model model) {
        try {
            memberService.updatePassword(dto, token);
            return "member/passwordupdatesuccess";
        } catch (IllegalStateException e) {
            model.addAttribute("errormsg", e.getMessage());
            return "member/find-password";
        }
    }
}
