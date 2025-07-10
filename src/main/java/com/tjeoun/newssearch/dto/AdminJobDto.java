package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.enums.AdminJobsEnum;
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

    public static AdminJobDto fromEntity(AdminJob job) {
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
                    case NEWS -> "admin/news/edit?id=" + job.getTargetId();
                    case BOARD_REPORT -> "admin/board/edit?id=" + job.getTargetId();
                    // todo : change this url after board, news is merged
                    case BOARD_REPLY_REPORT -> "board/detail?id=" + job.getTargetId();
                    case NEWS_REPLY_REPORT -> "news/edit?id=" + job.getTargetId();
                })
                .build();
    }

}
