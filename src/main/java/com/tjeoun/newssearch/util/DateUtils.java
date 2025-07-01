package com.tjeoun.newssearch.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
  public static String convertDate(String rawDate) {
    try {
      // 1. 마침표 제거 및 공백 제거
      rawDate = rawDate.replaceAll("\\.", "").trim(); // "20250626 오후 4:29"

      // 2. 오전/오후 → AM/PM으로 변경 (영문 Locale에 맞춰야 하기 때문)
      rawDate = rawDate.replace("오전", "AM").replace("오후", "PM");

      // 3. 공백 기준으로 포맷 재구성 → "2025 06 26 PM 4:29"
      String formatted = rawDate.substring(0, 4) + " " +
        rawDate.substring(4, 6) + " " +
        rawDate.substring(6, 8) + " " +
        rawDate.substring(8).trim();

      // 4. 파싱
      SimpleDateFormat parser = new SimpleDateFormat("yyyy MM dd a h:mm", Locale.ENGLISH);
      Date date = parser.parse(formatted);

      // 5. 원하는 포맷으로 반환
      return new SimpleDateFormat("yyyy-MM-dd").format(date);

    } catch (Exception e) {
      System.out.println("[날짜 변환 오류] " + rawDate);
      return "";
    }
  }
}
