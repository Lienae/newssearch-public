package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardReplyDto {
    private Long id;
    private String content;
    private Board board;
    private Member member;
    private String password;
    private Boolean isBlind;
}
