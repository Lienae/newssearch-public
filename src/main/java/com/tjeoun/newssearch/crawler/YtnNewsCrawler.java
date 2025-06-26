package com.tjeoun.newssearch.crawler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.util.DateUtils;
import com.tjeoun.newssearch.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Slf4j
@Component
public class YtnNewsCrawler {

  private static final String BASE_URL = "https://www.ytn.co.kr/_ln/";

  public List<Map<String, String>> fetchNewsList(String mcd, int page, List<String> pivots) {
    String url = "https://www.ytn.co.kr/ajax/getMoreNews.php";

    Map<String, String> headers = Map.of(
      "User-Agent", "Mozilla/5.0",
      "X-Requested-With", "XMLHttpRequest",
      "Content-Type", "application/x-www-form-urlencoded; charset=UTF-8"
    );

    Map<String, String> form = new LinkedHashMap<>();
    form.put("mcd", mcd);
    form.put("hcd", "");
    form.put("page", String.valueOf(page));

    // ✅ pivot[] 다중 파라미터 추가
    for (String pivot : pivots) {
      form.put("pivot[]", pivot);
    }

    List<Map<String, String>> result = new ArrayList<>();

    try {
      // Jsoup은 POST form 전송이 복잡하므로 OkHttp나 Jsoup 연결 직접 구성
      org.jsoup.Connection connection = Jsoup.connect(url)
        .headers(headers)
        .timeout(10000)
        .ignoreContentType(true)
        .method(org.jsoup.Connection.Method.POST);

      for (Map.Entry<String, String> entry : form.entrySet()) {
        connection.data(entry.getKey(), entry.getValue());
      }

      org.jsoup.Connection.Response response = connection.execute();

      // ✅ JSON 파싱
      String json = response.body();
      com.fasterxml.jackson.databind.JsonNode root =
        new com.fasterxml.jackson.databind.ObjectMapper().readTree(json);

      for (com.fasterxml.jackson.databind.JsonNode item : root.get("data")) {
        String title = item.get("title").asText();
        String joinKey = item.get("join_key").asText();

        Map<String, String> map = new HashMap<>();
        map.put("title", title);
        map.put("join_key", joinKey);
        result.add(map);
      }

    } catch (Exception e) {
      log.error("[YTN AJAX 리스트 크롤링 실패]: {}", e.getMessage());
    }

    return result;
  }

  // 상세 기사 + 이미지 저장 + DTO로 변환

  public NewsDto crawlAndConvertToDto(String mcd, String joinKey, String title) {
    try {
      String url = BASE_URL + mcd + "_" + joinKey;
      Document doc = Jsoup.connect(url).get();

      String content = Optional.ofNullable(doc.selectFirst("#CmAdContent")).map(Element::text).orElse("");
      String rawDate = Optional.ofNullable(doc.selectFirst(".date")).map(Element::text).orElse("");

      // DateUtils에서 변환한 문자열 (yyyy-MM-dd) 가져오기
      String formattedDateStr = DateUtils.convertDate(rawDate);

      // ✅ [앵커]가 포함된 기사라면 저장하지 않음
      if (content.contains("[앵커]")) {
        log.info("[앵커] 포함된 기사 무시: {}", title);
        return null;
      }
      // 변환된 문자열을 LocalDateTime으로 변환 (자정 기준)
      LocalDateTime date;
      if (!formattedDateStr.isEmpty()) {
        LocalDate localDate = LocalDate.parse(formattedDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA));
        date = localDate.atStartOfDay();
      } else {
        date = LocalDateTime.now();
      }

      String author = extractReporter(content);

      String imageUrl = Optional.ofNullable(doc.selectFirst("meta[property=og:image]"))
        .map(el -> el.attr("content")).orElse("");
      if (imageUrl.contains("ytn_sns_default")) imageUrl = "";

      // 이미지 저장
      String savedImagePath = "";
      if (!imageUrl.isEmpty()) {
        String folderPath = "images/YTN/" + date.toLocalDate();
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        savedImagePath = FileUtils.saveImage(imageUrl, folderPath, fileName);
      }

      return NewsDto.builder()
        .url(url)
        .title(title)
        .content(content)
        .publishDate(date)
        .author(author)
        .imageUrl(savedImagePath)
        .mediaCompany(NewsMediaCompany.YTN)
        .category(resolveCategory(mcd))
        .build();
    } catch (Exception e) {
      log.error("[YTN 상세 크롤링 실패]: {}", e.getMessage());
      return null;
    }
  }


  private NewsCategory resolveCategory(String mcd) {
    return switch (mcd) {
      case "0101" -> NewsCategory.POLITICS;
      case "0102" -> NewsCategory.ECONOMY;
      case "0103" -> NewsCategory.SOCIAL;
      case "0106" -> NewsCategory.CULTURE;
      case "0107" -> NewsCategory.SPORTS;
      default -> NewsCategory.MISC;
    };
  }
  private String extractReporter(String content) {
    if (content == null) return "";

    List<String> patterns = List.of(
      "YTN\\s*([가-힣]{2,6})\\s*\\([a-zA-Z0-9._%+-]+@ytn\\.co\\.kr\\)",
      "YTN\\s*([가-힣]{2,6})\\s*\\([a-zA-Z0-9._%+-]+@ytnradio.kr\\)",
      "YTN\\s*([가-힣]{2,6})\\s*\\(",
      "\\s*([가-힣]{2,6})\\s*\\([a-zA-Z0-9._%+-]+@ytn\\.co\\.kr\\)",
      "기자\\s*[:：]\\s*([가-힣]{2,6})",
      "취재기자\\s*[:：]\\s*([가-힣]{2,6})",
      "보도에\\s+([가-힣]{2,6})\\s*기자",
      "([가-힣]{2,6})\\s*입니다\\s*\\(YTN\\)",
      "취재\\s*[:：]?\\s*([가-힣]{2,6})",
      "([가-힣]{2,6})\\s*(기자|특파원|PD)\\s*$",
      "\\[사진\\s*=\\s*([가-힣]{2,6})\\s*기자\\]",
      "\\((?:사진|영상)?\\s*([가-힣]{2,6})\\s*기자\\)"
    );

    for (String regex : patterns) {
      java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(regex).matcher(content);
      if (matcher.find()) {
        return matcher.group(1).trim();
      }
    }

    return "";
  }

}
