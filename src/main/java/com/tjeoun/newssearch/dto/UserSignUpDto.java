package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.enums.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSignUpDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private UserRole role;
}
