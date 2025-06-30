package com.tjeoun.newssearch.util;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
  public static String saveImage(String imageUrl, String folderPath, String fileName) {
    try {
      Path dir = Path.of(folderPath);
      Files.createDirectories(dir);

      Path filePath = dir.resolve(fileName);
      try (InputStream in = new URL(imageUrl).openStream()) {
        Files.copy(in, filePath);
      }

      return folderPath + "/" + fileName; // 저장된 상대경로 반환
    } catch (IOException e) {
      System.err.println("이미지 저장 실패: " + imageUrl);
      return null;
    }
  }
}
