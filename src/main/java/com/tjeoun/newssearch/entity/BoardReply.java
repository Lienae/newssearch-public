package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.BoardReplyDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String password;

    public static BoardReply createBoardReply(BoardReplyDto dto, PasswordEncoder passwordEncoder) {
        return BoardReply.builder()
                .id(dto.getId())
                .board(dto.getBoard())
                .member(dto.getMember())
                .content(dto.getContent())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
    }
}
