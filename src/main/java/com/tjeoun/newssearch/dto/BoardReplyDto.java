package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardReplyDto {
    public Long id;
    public String content;
    public Board board;
    public Member member;
    public String password;
}
