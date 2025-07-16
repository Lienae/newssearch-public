package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.entity.BoardReply;
import com.tjeoun.newssearch.enums.AdminJobsEnum;
import com.tjeoun.newssearch.repository.BoardReplyRepository;
import com.tjeoun.newssearch.repository.NewsReplyRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class AdminJobDto {

    private Long id;
    private AdminJobsEnum job;
    private String jobString;
    private Long targetId;
    private LocalDateTime recordedTime;
    private Boolean isResolved;
    private String url;

    public static AdminJobDto fromEntity(AdminJob job,
                                         NewsReplyRepository newsReplyRepository,
                                         BoardReplyRepository boardReplyRepository) {
        return AdminJobDto.builder()
                .id(job.getId())
                .jobString(switch(job.getJob()) {
                    case NEWS -> "뉴스 기사 오류";
                    case BOARD_REPORT -> "게시글 신고 접수";
                    case NEWS_REPLY_REPORT -> "뉴스 댓글 신고 접수";
                    case BOARD_REPLY_REPORT -> "게시글 댓글 신고 접수";
                })
                .targetId(job.getTargetId())
                .recordedTime(job.getRecordedTime())
                .isResolved(job.getIsResolved())
                .url(switch(job.getJob()) {
                    case NEWS -> "/admin/news/edit?id=" + job.getTargetId();
                    case BOARD_REPORT -> "/admin/boards/edit?id=" + job.getTargetId();
                    case BOARD_REPLY_REPORT -> "/board/detail/" + boardReplyRepository.findById(job.getTargetId()).get().getBoard().getId();
                    case NEWS_REPLY_REPORT -> "/news/view?id=" + newsReplyRepository.findById(job.getTargetId()).get().getNews().getId();
                })
                .build();
    }

}
