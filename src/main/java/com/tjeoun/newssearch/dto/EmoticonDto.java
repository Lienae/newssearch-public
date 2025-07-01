package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.EmoticonEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmoticonDto {
    private Long id;
    private Member member;
    private News news;
    private EmoticonEnum emoticonEnum;
}
