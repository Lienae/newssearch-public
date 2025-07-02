package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class MemberDto {
  private Long id;
  private String name;
  private String email;
  private String password;
  private UserRole role;
  // private Boolean isBlind;
  private LocalDateTime createdDate;
  private LocalDateTime lastModifiedDate;
}
