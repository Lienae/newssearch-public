package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.MemberDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

  private final MemberRepository memberRepository;

  @GetMapping("/list")
  public String list(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "10") int size,
                     Model model) {
    Page<Member> members = memberRepository.findAll(PageRequest.of(page, size));

    long totalCount = members.getTotalElements();

    model.addAttribute("members", members);
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    model.addAttribute("totalCount", totalCount);
    return "admin/user-list";
  }


  @GetMapping("/edit")
  public String editForm(@RequestParam Long id,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         Model model) {
    Member member = memberRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Member not found"));

    MemberDto dto = MemberDto.builder()
      .id(member.getId())
      .name(member.getName())
      .email(member.getEmail())
      .role(member.getRole())
      //.isBlind(member.getIsBlind())
      .createdDate(member.getCreatedDate())
      .lastModifiedDate(member.getLastModifiedDate())
      .build();

    model.addAttribute("member", dto);
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    return "admin/user-edit";
  }

  @PostMapping("/edit/{id}")
  public String edit(@PathVariable Long id,
                     @ModelAttribute MemberDto dto,
                     @RequestParam int page,
                     @RequestParam int size,
                      Model model) {
    Member member = memberRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Member not found"));
    member.setName(dto.getName());
    member.setPassword(dto.getPassword());
    member.setRole(dto.getRole());
    memberRepository.save(member);
    return "redirect:/admin/members/list?page=" + page + "&size=" + size;
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    memberRepository.deleteById(id);
    return "redirect:/admin/members/list";
  }

  // blind 컬럼 생기면 사용 예정
//  @PostMapping("/delete/{id}")
//  public String delete(@PathVariable Long id) {
//    Member member = memberRepository.findById(id)
//      .orElseThrow(() -> new RuntimeException("Member not found"));
//    member.setIsBlind(true);
//    memberRepository.save(member);
//    return "redirect:/api/v1/member/list";
//  }


}
