package com.tjeoun.newssearch.dto;

import com.tjeoun.newssearch.entity.AttachFile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminAttachFileDto {
  private String fileName;
  private Long fileSize;

  public static AdminAttachFileDto fromEntity(AttachFile file) {
    return AdminAttachFileDto.builder()
      .fileName(file.getOriginalFilename())
      .fileSize(file.getSize())
      .build();
  }
}
