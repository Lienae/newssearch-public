package com.tjeoun.newssearch.helper;

import com.tjeoun.newssearch.util.DateUtils;
import com.tjeoun.newssearch.util.NewsCrawlUtils;
import io.micrometer.common.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.Connection;
import org.jsoup.Connection.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.*;

import static com.tjeoun.newssearch.util.NewsCrawlUtils.createNewsMap;
import static com.tjeoun.newssearch.util.NewsCrawlUtils.ytnImageBasePath;

public class YtnNewsCrawlerHelper {

    private static final String BASE_URL = "https://www.ytn.co.kr/_ln/";

    public static List<Map<String, String>> collectArticles() {
        String[] categories = { "0101", "0102", "0103", "0106", "0107" };
        int targetCount = 30;
        List<Map<String, String>> articles = new ArrayList<>();
        for (String mcd : categories) {
            int page = 1, count = 0;
            // 중복 방지용 세트/리스트
            List<String> pivots = new ArrayList<>();
            Set<String> seenJoinKeys = new HashSet<>();

            while (true) {
                List<Map<String, String>> articleUrls = YtnNewsCrawlerHelper.fetchNewsList(mcd, page, pivots);
                if (articleUrls.isEmpty()) break;

                for (Map<String, String> item : articleUrls) {
                    String joinKey = item.get("join_key");
                    String title = item.get("title");

                    // 중복 확인
                    if (joinKey == null || seenJoinKeys.contains(joinKey)) continue;

                    // 본문 + 날짜 + 기자 + 썸네일 크롤링
                    Map<String, String> newsMap = YtnNewsCrawlerHelper.newsCrawl(mcd, joinKey, title);
                    if (newsMap == null) {
                        pivots.add(joinKey); // 실패했어도 pivot으로 등록
                        continue;
                    }

                    // 등록된 기사 처리
                    seenJoinKeys.add(joinKey);
                    pivots.add(joinKey);
                    count++;
                    articles.add(newsMap);
                    if (count >= targetCount) break;
                }
                page++;
            }
        }
        return articles;
    }
    public static List<Map<String, String>> fetchNewsList(String mcd, int page, List<String> pivots) {
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

        // pivot[] 다중 파라미터 추가
        for (String pivot : pivots) {
            form.put("pivot[]", pivot);
        }

        List<Map<String, String>> result = new ArrayList<>();

        try {
            // Jsoup은 POST form 전송이 복잡하므로 OkHttp나 Jsoup 연결 직접 구성
            Connection connection = Jsoup.connect(url)
                    .headers(headers)
                    .timeout(10000)
                    .ignoreContentType(true)
                    .method(Connection.Method.POST);

            for (Map.Entry<String, String> entry : form.entrySet()) {
                connection.data(entry.getKey(), entry.getValue());
            }

            Response response = connection.execute();

            // JSON 파싱
            String json = response.body();
            if(!StringUtils.isBlank(json)) {
                JsonNode root = new ObjectMapper().readTree(json);

                for (JsonNode item : root.get("data")) {
                    String title = item.get("title").asText();
                    String joinKey = item.get("join_key").asText();

                    Map<String, String> map = new HashMap<>();
                    map.put("title", title);
                    map.put("join_key", joinKey);
                    result.add(map);
                }
            }

        } catch (Exception e) {
            System.err.println("[YTN AJAX 리스트 크롤링 실패]: " + e.getMessage());
        }

        return result;
    }

    // 상세 기사 + 이미지 저장 + DTO로 변환
    public static Map<String, String> newsCrawl(String mcd, String joinKey, String title) {
        try {
            String url = BASE_URL + mcd + "_" + joinKey;
            Document doc = Jsoup.connect(url).get();

            String content = Optional.ofNullable(doc.selectFirst("#CmAdContent")).map(Element::text).orElse("");
            String rawDate = Optional.ofNullable(doc.selectFirst(".date")).map(Element::text).orElse("");

            // DateUtils에서 변환한 문자열 (yyyy-MM-dd) 가져오기
            String formattedDateStr = DateUtils.convertDate(rawDate);


            // 변환된 문자열을 LocalDate으로 변환 (자정 기준)
            if (formattedDateStr.isEmpty()) {
                formattedDateStr = LocalDate.now().toString();
            }

            String author = extractReporter(doc);

            String imageUrl = Optional.ofNullable(doc.selectFirst("meta[property=og:image]"))
                    .map(el -> el.attr("content")).orElse("");
            if (imageUrl.contains("ytn_sns_default")) imageUrl = "";

            // 이미지 저장
            String savedImagePath = StringUtils.isBlank(imageUrl)
                    ? ""
                    : NewsCrawlUtils.downloadImage(imageUrl, ytnImageBasePath);

            return createNewsMap("YTN", resolveCategory(mcd), title, content,
                    author, formattedDateStr, url, savedImagePath);

        } catch (Exception e) {
            System.err.println("[YTN 상세 크롤링 실패]: " + e.getMessage());
            return null;
        }
    }

    private static String resolveCategory(String mcd) {
        return switch (mcd) {
            case "0101" -> "정치";
            case "0102" -> "경제";
            case "0103" -> "사회";
            case "0106" -> "문화";
            case "0107" -> "스포츠";
            default -> "기타";
        };
    }

    private static String extractReporter(Document doc) {
        Element authorMeta = doc.selectFirst("meta[name=author]");
        if (authorMeta != null) {
            String content = authorMeta.hasAttr("content") ? authorMeta.attr("content").trim() : "";
            if (!content.isBlank() && !content.equalsIgnoreCase("YTN")) {
                return content;
            }
        }
        return "";
    }


}
