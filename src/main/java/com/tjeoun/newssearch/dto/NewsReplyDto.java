package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.entity.NewsReply;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsReplyDto {

    private Long id;
    private Long newsId;
    private String content;
    private String writerName;
    private String url;
    private String writerEmail;

    // private News news;
    // private Member member;

    // NewsReply → DTO 변환용 생성자
    public NewsReplyDto(NewsReply reply) {
        this.id = reply.getId();
        this.newsId = reply.getNews().getId();
        this.content = reply.getContent();
        this.writerName = reply.getMember().getName(); // Member 엔티티에 name 필드 있어야 함
        this.writerEmail = reply.getMember().getEmail();
        this.url = reply.getNews().getUrl();
    }
}