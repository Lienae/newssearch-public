package com.tjeoun.newssearch.helper;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.tjeoun.newssearch.util.NewsCrawlUtils;
import io.micrometer.common.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;

import org.jsoup.nodes.Element;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.tjeoun.newssearch.util.NewsCrawlUtils.DONGA_RSS_MAP;
import static com.tjeoun.newssearch.util.NewsCrawlUtils.downloadImage;

public class DongaCrawlerHelper {

    public static List<Map<String, String>> collectArticles() {
        List<Map<String, String>> articles = new ArrayList<>();
        DONGA_RSS_MAP.forEach((category, url) -> {
            try {
                SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
                for (SyndEntry entry : feed.getEntries()) {
                    Map<String, String> article = parseArticle(entry, category);
                    if (article != null) {
                        articles.add(article);
                        Thread.sleep(500); // 부하 방지
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return articles;
    }
    private static Map<String, String> parseArticle(SyndEntry entry, String category) {
        try {
            String link = entry.getLink();
            Document doc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // 본문 추출
            String content = doc.select("section.news_view").text();


            // 기자명
            String reporter = doc.select("meta[property=dd:author]").attr("content");

            // 날짜
            String publishedDate;
            if (entry.getPublishedDate() != null) {
                publishedDate = entry.getPublishedDate()
                        .toInstant().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } else {
                publishedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

            // 이미지 추출 + 저장
            String imageUrl = extractThumbnailUrl(link);
            String savedImagePath = StringUtils.isBlank(imageUrl)
                    ? ""
                    :downloadImage(imageUrl, publishedDate); // 저장된 경로

            return NewsCrawlUtils.createNewsMap("동아일보", category, entry.getTitle(),
                    content, reporter, publishedDate, link, savedImagePath);
        } catch (Exception e) {
            System.err.println("기사 파싱 실패: " + entry.getLink() + " → " + e.getMessage());
            return null;
        }
    }
    private static String extractThumbnailUrl(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            Element ogImage = doc.selectFirst("meta[property=og:image]");
            if (ogImage != null) {
                String content = ogImage.attr("content");
                if (content.contains("logo") || content.contains("CI")) return "";
                return content;
            }

            Element img = doc.selectFirst("div.article_img img");
            if (img != null) return img.attr("src");

        } catch (Exception e) {
            System.err.println("이미지 추출 실패: " + url + " → " + e.getMessage());
        }
        return "";
    }
}