package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.NewsReplyDto;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.entity.NewsReply;
import com.tjeoun.newssearch.enums.UserRole;
import com.tjeoun.newssearch.repository.MemberRepository;
import com.tjeoun.newssearch.repository.NewsReplyRepository;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsCommentService {

    private final NewsReplyRepository newsReplyRepository;
    private final NewsRepository newsRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public NewsReplyDto addComment(NewsReplyDto dto, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        // URL 기준으로 뉴스 검색
        News news = newsRepository.findByUrl(dto.getUrl())
                .orElseThrow(() -> new RuntimeException("뉴스 없음"));

        NewsReply reply = NewsReply.builder()
                .member(member)
                .news(news)
                .content(dto.getContent())
                .isBlind(false)
                .build();

        newsReplyRepository.save(reply);
        return new NewsReplyDto(reply);
    }

    @Transactional
    public void deleteComment(Long commentId, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        NewsReply reply = newsReplyRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!reply.getMember().getId().equals(member.getId()) && !member.getRole().equals(UserRole.ADMIN)) {
            throw new RuntimeException("본인 댓글만 삭제할 수 있습니다.");
        }

        // soft delete 처리 (isBlind 필드 필요)
        reply.setIsBlind(true);
    }

    @Transactional
    public void updateComment(Long commentId, String newContent, String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        NewsReply reply = newsReplyRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 작성자 체크 (로그인 후 활성화)
        if (!reply.getMember().getId().equals(member.getId())) {
            throw new RuntimeException("본인 댓글만 수정할 수 있습니다");
        }

        reply.setContent(newContent);
        reply.setModifiedDate(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<NewsReplyDto> findCommentsByNewsUrl(String url) {
        News news = newsRepository.findByUrl(url)
                .orElseThrow(() -> new RuntimeException("해당 뉴스 URL이 존재하지 않습니다."));

        List<NewsReply> replies = newsReplyRepository.findByNewsAndIsBlindFalse(news);
        return replies.stream()
                .map(NewsReplyDto::new)
                .collect(Collectors.toList());
    }

}