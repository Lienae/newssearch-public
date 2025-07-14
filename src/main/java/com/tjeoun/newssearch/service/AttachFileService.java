package com.tjeoun.newssearch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachFileService {

  @Value("${attachFileLocation}")
  private String attachFileLocation;

  public String saveFile(String originalFilename, byte[] fileData) throws IOException {
    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
    String savedFilename = UUID.randomUUID() + extension;
    String filePath = attachFileLocation + "/" + savedFilename;

    // 폴더 존재 여부 확인 + 없으면 생성
    File dir = new File(attachFileLocation);
    if (!dir.exists()) {
      boolean created = dir.mkdirs();
      if (created) {
        log.info("폴더 생성: {}", attachFileLocation);
      } else {
        log.warn("폴더 생성 실패: {}", attachFileLocation);
        throw new IOException("폴더 생성 실패");
      }
    }
    try (FileOutputStream fos = new FileOutputStream(filePath)) {
      fos.write(fileData);
      log.info("파일 저장 성공: {}", savedFilename);
    } catch (IOException e) {
      log.error("파일 저장 실패", e);
      throw e;
    }



    return savedFilename;
  }
  public String getUploadDir() {
    return this.attachFileLocation;
  }


  public void deleteFile(String serverFilename) {
    File file = new File(attachFileLocation + "/" + serverFilename);
    if (file.exists()) {
      file.delete();
      log.info("파일 삭제: {}", serverFilename);
    } else {
      log.info("파일 없음: {}", serverFilename);
    }
  }
}

