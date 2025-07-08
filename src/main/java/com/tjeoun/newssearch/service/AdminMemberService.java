package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.AdminMemberDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

  public Page<AdminMemberDto> getMembers(int page, int size, String searchType, String keyword) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastModifiedDate"));

    Page<Member> result;

    if (keyword != null && !keyword.isBlank()) {
      if ("name".equals(searchType)) {
        result = memberRepository.findByNameContainingIgnoreCase(keyword, pageable);
      } else if ("email".equals(searchType)) {
        result = memberRepository.findByEmailContainingIgnoreCase(keyword, pageable);
      } else {
        result = memberRepository.findAll(pageable);
      }
    } else {
      result = memberRepository.findAll(pageable);
    }

    return result.map(AdminMemberDto::fromEntity);
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
    member.setBlind(Boolean.TRUE.equals(dto.getIsBlind()));
  }

  @Transactional
  public void softDeleteMember(Long id) {
    Member member = memberRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("Member not found"));
    member.setBlind(true);
  }
}

