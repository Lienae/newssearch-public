package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.Board;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachFileDto {
    private String fileName;
    private Long fileSize;
    private Board board;
}
