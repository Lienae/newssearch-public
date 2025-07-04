package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.AdminMemberDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminMemberService {

  private final MemberRepository memberRepository;

  public Page<AdminMemberDto> getMembers(int page, int size) {
    Page<Member> members = memberRepository.findByIs_blindFalse(PageRequest.of(page, size));
    return members.map(AdminMemberDto::fromEntity);
  }

  public AdminMemberDto getMemberDto(Long id) {
    Member member = memberRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Member not found"));

    return AdminMemberDto.fromEntity(member);
  }

  @Transactional
  public void updateMember(Long id, AdminMemberDto dto) {
    Member member = memberRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Member not found"));
    member.setName(dto.getName());
    member.setPassword(dto.getPassword());
    member.setRole(dto.getRole());
  }

  @Transactional
  public void softDeleteMember(Long id) {
    Member member = memberRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Member not found"));
    member.set_blind(true);
  }
}

