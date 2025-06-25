package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardDto {
    private Long id;
    private String title;
    private String content;
    private Member author;
    private String password;
}
