package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.enums.UserRole;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpDto {
    private Long id;
    private String email;
    private String password;
    private String name;
    private UserRole role;
}
