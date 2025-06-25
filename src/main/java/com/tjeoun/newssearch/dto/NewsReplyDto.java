package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.News;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NewsReplyDto {
    public Long id;
    public String content;
    public News news;
    public Member member;
    public String password;
}
