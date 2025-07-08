package com.tjeoun.newssearch.helper;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.tjeoun.newssearch.util.NewsCrawlUtils;
import io.micrometer.common.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.tjeoun.newssearch.util.NewsCrawlUtils.KHAN_RSS_MAP;
import static com.tjeoun.newssearch.util.NewsCrawlUtils.createNewsMap;

public class KhanCrawlerHelper {
    public static List<Map<String, String>> collectAllArticles() {
        List<Map<String, String>> all = new ArrayList<>();
        for (Map.Entry<String, String> entry : KHAN_RSS_MAP.entrySet()) {
            List<Map<String, String>> articles = collectFromRss(entry.getValue(), entry.getKey());
            all.addAll(articles.stream().toList());
        }
        return all;
    }

    public static List<Map<String, String>> collectFromRss(String rssUrl, String category) {
        List<Map<String, String>> result = new ArrayList<>();
        try (XmlReader reader = new XmlReader(new URL(rssUrl))) {
            SyndFeed feed = new SyndFeedInput().build(reader);
            List<SyndEntry> entries = feed.getEntries();

            for (SyndEntry entry : entries) {
                String title = entry.getTitle();
                String link = entry.getLink();
                String content = extractFullText(link);
                String author = cleanAuthor(entry.getAuthor());
                if (StringUtils.isBlank(author)) author = "";
                String dateStr = entry.getPublishedDate() != null
                        ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(entry.getPublishedDate())
                        : "";
                String imageUrl = extractThumbnail(link);
                String savedImagePath = StringUtils.isBlank(imageUrl)
                        ? ""
                        : NewsCrawlUtils.downloadImage(imageUrl, NewsCrawlUtils.khanImageBasePath);

                Map<String, String> articleData = createNewsMap("KHAN", category, title, content,
                        author, dateStr, link, savedImagePath);

                result.add(articleData);
                Thread.sleep(500);
            }


        } catch (Exception e) {
            System.err.println("RSS 수집 오류: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
    private static String extractFullText(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get();
        Element contentDiv = doc.selectFirst("div#articleBody");
        if (contentDiv == null) return "";
        Elements paragraphs = contentDiv.select("p");
        return paragraphs.stream().map(Element::text).collect(Collectors.joining("\n"));
    }

    private static String cleanAuthor(String raw) {
        if (raw == null) return "";
        raw = raw.replaceAll("\\S+@\\S+", "");
        Set<String> names = new LinkedHashSet<>();

        Matcher m1 = Pattern.compile("[가-힣]{2,10}\\s*\\|\\s*[가-힣]{2,4}").matcher(raw);
        while (m1.find()) names.add(m1.group().split("\\|")[1].trim());

        Matcher m2 = Pattern.compile("([가-힣]{2,4})\\s*(기자|선임기자|특파원|수습기자|인턴|논설위원)").matcher(raw);
        while (m2.find()) names.add(m2.group(1));

        for (String part : raw.split("[,|/]")) {
            part = part.replaceAll("(기자|선임기자|수습기자|특파원|인턴|논설위원)", "").trim();
            if (part.matches("[가-힣]{2,4}")) names.add(part);
        }
        return String.join(",", names);
    }

    private static String extractThumbnail(String url) throws IOException {
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get();
        Element ogImage = doc.selectFirst("meta[property=og:image]");
        if (ogImage != null) {
            String content = ogImage.attr("content");
            if (content.contains("Khan_CI_180212") || content.contains("khan_logo")) {
                return "";
            }
            return content;
        }
        Element img = doc.selectFirst("div.photo img");
        if (img != null) return img.attr("src");
        return "";
    }
}
