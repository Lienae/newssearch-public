package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.AdminLogEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AdminLogDTO {
    private AdminLogEnum action;
    private String ipAddress;
    private Long targetId;
    private String targetTable;
    private Member admin;

}
