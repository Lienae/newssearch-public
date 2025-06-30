package com.tjeoun.newssearch.util;



import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.rometools.rome.io.XmlReader;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;


@Component
public class RssUtils {

  private static final Map<NewsCategory, String> rssFeeds = Map.of(
    NewsCategory.POLITICS, "https://www.khan.co.kr/rss/rssdata/politic_news.xml",
    NewsCategory.ECONOMY, "https://www.khan.co.kr/rss/rssdata/economy_news.xml",
    NewsCategory.SOCIAL, "https://www.khan.co.kr/rss/rssdata/society_news.xml",
    NewsCategory.CULTURE, "https://www.khan.co.kr/rss/rssdata/culture_news.xml",
    NewsCategory.SPORTS, "https://www.khan.co.kr/rss/rssdata/kh_sports.xml"
  );

  private static final String DEFAULT_IMAGE_PATH = "images/khan/default.png";

  private static final Logger log = LoggerFactory.getLogger(RssUtils.class);

  public List<NewsDto> collectAllArticles(int limitPerCategory) {
    List<NewsDto> all = new ArrayList<>();
    for (Map.Entry<NewsCategory, String> entry : rssFeeds.entrySet()) {
      List<NewsDto> articles = collectFromRss(entry.getValue(), entry.getKey(), limitPerCategory);
      all.addAll(articles.stream().limit(20).collect(Collectors.toList()));
    }
    return all;
  }

  private List<NewsDto> collectFromRss(String rssUrl, NewsCategory category, int limit) {
    List<NewsDto> result = new ArrayList<>();
    try (XmlReader reader = new XmlReader(new URL(rssUrl))) {
      SyndFeed feed = new SyndFeedInput().build(reader);
      List<SyndEntry> entries = feed.getEntries().subList(0, Math.min(limit, feed.getEntries().size()));

      for (SyndEntry entry : entries) {
        String title = entry.getTitle();
        String link = entry.getLink();
        String content = extractFullText(link);
        String author = cleanAuthor(entry.getAuthor());
        if (author == null) author = "";

        String dateStr = entry.getPublishedDate() != null
          ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(entry.getPublishedDate())
          : "";
        LocalDateTime publishDate = !dateStr.isBlank()
          ? LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay()
          : LocalDateTime.now();

        String imageUrl = extractThumbnail(link);
        String savedImagePath = downloadImage(imageUrl, dateStr);

        NewsDto dto = NewsDto.builder()
          .url(link)
          .title(title)
          .content(content)
          .author(author)
          .publishDate(publishDate)
          .imageUrl(savedImagePath)
          .category(category)
          .mediaCompany(NewsMediaCompany.KHAN)
          .build();

        result.add(dto);
        Thread.sleep(500);
      }


    } catch (Exception e) {
      log.error("RSS 수집 오류: {}", e.getMessage());
    }

    return result;
  }

  private String extractFullText(String url) {
    try {
      Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get();
      Element contentDiv = doc.selectFirst("div#articleBody");
      if (contentDiv == null) return "";
      Elements paragraphs = contentDiv.select("p");
      return paragraphs.stream().map(Element::text).collect(Collectors.joining("\n"));
    } catch (Exception e) {
      return "";
    }
  }

  private String cleanAuthor(String raw) {
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

  private String extractThumbnail(String url) {
    try {
      Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(5000).get();
      Element ogImage = doc.selectFirst("meta[property=og:image]");
      if (ogImage != null) {
        String content = ogImage.attr("content");
        if (content.contains("Khan_CI_180212") || content.contains("khan_logo")) {
          return "default";
        }
        return content;
      }

      Element img = doc.selectFirst("div.photo img");
      if (img != null) return img.attr("src");
    } catch (Exception e) {
      // ignore
    }
    return "default";
  }

  private String downloadImage(String imageUrl, String articleDate) {
    String saveDir = "images/khan/" + articleDate;
    File dir = new File(saveDir);
    if (!dir.exists()) dir.mkdirs();

    if (imageUrl.equals("default")) return DEFAULT_IMAGE_PATH;

    try {
      String originalPath = new URL(imageUrl).getPath();
      String extension = originalPath.contains(".")
        ? originalPath.substring(originalPath.lastIndexOf(".") + 1)
        : "jpg";

      if (!extension.matches("(?i)jpg|jpeg|png|gif")) {
        return DEFAULT_IMAGE_PATH;
      }

      String uuid = UUID.randomUUID().toString();
      String newFilename = uuid + "." + extension;

      File output = new File(saveDir, newFilename);
      if (output.exists()) return output.getPath();

      try (var in = new URL(imageUrl).openStream(); var out = new FileOutputStream(output)) {
        in.transferTo(out);
      }

      return output.getPath();
    } catch (Exception e) {
      return DEFAULT_IMAGE_PATH;
    }
  }
}
