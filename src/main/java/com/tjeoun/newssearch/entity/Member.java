package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.UserSignUpDto;
import com.tjeoun.newssearch.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public static Member createMenber(UserSignUpDto dto, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .id(dto.getId())
                .name(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }
}
