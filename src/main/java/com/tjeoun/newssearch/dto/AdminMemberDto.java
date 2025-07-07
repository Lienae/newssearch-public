package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AdminMemberDto {
  private Long id;
  private String name;
  private String email;
  private String password;
  private UserRole role;
  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;
  private Boolean isBlind;

  public static AdminMemberDto fromEntity(Member member) {
    return AdminMemberDto.builder()
      .id(member.getId())
      .name(member.getName())
      .email(member.getEmail())
      .role(member.getRole())
      .createdDate(member.getCreatedDate())
      .lastModifiedDate(member.getLastModifiedDate())
      .isBlind(member.isBlind())
      .build();
  }
}