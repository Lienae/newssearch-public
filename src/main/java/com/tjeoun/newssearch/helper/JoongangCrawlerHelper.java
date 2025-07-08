package com.tjeoun.newssearch.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.tjeoun.newssearch.util.NewsCrawlUtils.*;

public class JoongangCrawlerHelper {
    public static List<Map<String, String>> getJoongangArticles() {
        List<Map<String, String>> articles = new ArrayList<>();
        for (Map.Entry<String, String> entry : JOONGANG_SITE_MAP.entrySet()) {
            List<String> links = getJoongangArticleLinks(USER_AGENT, entry.getValue());
            for (String link : links.stream().limit(20).toList()) {
                Map<String, String> article = parseArticle(USER_AGENT, link, joongangImageBasePath);
                if (article != null) {
                    article.put("카테고리", entry.getKey());
                    articles.add(article);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
        return articles;
    }
    public static List<String> getJoongangArticleLinks(String USER_AGENT, String listUrl) {
        List<String> urls = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(listUrl)
                    .userAgent(USER_AGENT)
                    .referrer("https://www.google.com")
                    .get();
            Elements links = doc.select("ul.story_list > li > div.card_body > h2 > a");
            for (Element link : links) {
                String href = link.attr("href");
                if (!href.startsWith("http")) {
                    href = "https://www.joongang.co.kr" + href;
                }
                urls.add(href);
            }
        } catch (Exception e) {
            System.err.println("[오류] 목록 페이지 접속 실패: " + listUrl + " - " + e.getMessage());
        }
        return urls;
    }

    public static Map<String, String> parseArticle(String USER_AGENT, String url, String imageBasePath) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .referrer("https://www.google.com")
                    .timeout(10000)
                    .get();

            // 제목
            String title = Objects.requireNonNull(doc.selectFirst("h1.headline")).text();

            // 본문
            String content = "본문 없음";
            Element contentDiv = doc.selectFirst("div#article_body");
            if (contentDiv != null) {
                Elements paragraphs = contentDiv.select("p[data-divno]");
                StringBuilder sb = new StringBuilder();
                for (Element p : paragraphs) {
                    sb.append(p.text()).append("\n");
                }
                content = sb.toString().trim();
            }

            // 기자명
            String reporter = "";
            Element authorMeta = doc.selectFirst("meta[property=dable:author]");
            if (authorMeta != null) {
                reporter = authorMeta.hasAttr("content") ? authorMeta.attr("content").trim() : "";
                if (reporter.isBlank()) reporter = "";
            }

            // 날짜
            String pubDate = "";
            Element dateTag = doc.selectFirst("time[itemprop=dateModified]");
            if (dateTag == null) {
                dateTag = doc.selectFirst("time[itemprop=datePublished]");
            }
            if (dateTag != null) {
                String dateStr = dateTag.attr("datetime");
                OffsetDateTime odt = OffsetDateTime.parse(dateStr);
                pubDate = odt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            // 이미지
            Element ogImageTag = doc.selectFirst("meta[property=og:image]");
            String imageUrl = ogImageTag != null ? ogImageTag.attr("content") : "";
            String savedImagePath;
            if (!imageUrl.isBlank()) {
                savedImagePath = downloadImage(imageUrl, imageBasePath);
            } else {
                savedImagePath = defaultImageBasePath;
            }

            Map<String, String> result = new LinkedHashMap<>();
            result.put("언론사", "중앙");
            result.put("링크", url);
            result.put("제목", title);
            result.put("기자명", reporter);
            result.put("날짜", pubDate);
            result.put("내용", content);
            result.put("대표이미지", savedImagePath);

            return result;

        } catch (Exception e) {
            System.err.println("[에러] " + url + " 파싱 실패: " + e.getMessage());
            return null;
        }
    }
}
