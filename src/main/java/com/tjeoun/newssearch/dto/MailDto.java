package com.tjeoun.newssearch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MailDto {
    private String to;
    private String subject;
    private String body;
    private List<MultipartFile> attachments;
}
