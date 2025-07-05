package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.SignUpDto;
import com.tjeoun.newssearch.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Column(nullable = false)
    private boolean is_blind; // 삭제 처리를 위해 추가하였습니다!

    public static Member createMember(SignUpDto dto, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .name(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .role(dto.getRole())
                .build();
    }

    public List<UserRole> getRoleList() {
        if(this.role == null) return Collections.emptyList();
        return List.of(this.role);
    }
}
