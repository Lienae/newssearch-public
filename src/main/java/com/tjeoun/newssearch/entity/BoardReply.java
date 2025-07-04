package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.BoardReplyDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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

    // DB에서 직접 삭제하고 싶었는데 실행할 때마다 새로 생성되어서 null 허용했습니다
    @Column(nullable = true)
    private String password;


    // 임의로 넣었습니다
    @Column(nullable = false)
    private Boolean isBlind;



    public static BoardReply createBoardReply(BoardReplyDto dto) {
        return BoardReply.builder()
                .id(dto.getId())
                .board(dto.getBoard())
                .member(dto.getMember())
                .content(dto.getContent())
                .isBlind(false) // 댓글 생성 시 기본은 false
                .build();
    }
}
