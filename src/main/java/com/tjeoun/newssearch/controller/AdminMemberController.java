package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.AdminMemberDto;
import com.tjeoun.newssearch.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String searchType,
                       @RequestParam(required = false) String keyword,
                       Model model) {

        Page<AdminMemberDto> members = adminMemberService.getMembers(page, size, searchType, keyword);


    model.addAttribute("members", members);
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    model.addAttribute("totalCount", members.getTotalElements());
    model.addAttribute("searchType", searchType);
    model.addAttribute("keyword", keyword);
    return "admin/user-list";
  }


    @GetMapping("/edit")
    public String editForm(@RequestParam Long id,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        AdminMemberDto dto = adminMemberService.getMemberDto(id);
        model.addAttribute("member", dto);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        return "admin/user-edit";
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute AdminMemberDto dto,
                       @RequestParam int page,
                       @RequestParam int size,
                       Model model) {
        try {
            adminMemberService.updateMember(id, dto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "회원 정보 수정 실패: " + e.getMessage());

            return "error/error";
        }

        return "redirect:/admin/members/list?page=" + page + "&size=" + size + "&success=update";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        adminMemberService.softDeleteMember(id);
        return "redirect:/admin/members/list?success=delete";
    }
}

