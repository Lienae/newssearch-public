package com.tjeoun.newssearch.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.nodes.Element;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewsCollectorService {

    private final NewsRepository newsRepository;
    private final NewsDocumentRepository newsDocumentRepository;

    private static final String DEFAULT_IMAGE_PATH = "images/default.png";
    private static final String BASE_IMAGE_SAVE_DIR = "images/donga/";
    private static final Map<NewsCategory, String> rssFeeds = new LinkedHashMap<>(Map.of(
            NewsCategory.POLITICS, "https://rss.donga.com/politics.xml",
            NewsCategory.ECONOMY, "https://rss.donga.com/economy.xml",
            NewsCategory.SOCIAL, "https://rss.donga.com/national.xml",
            NewsCategory.CULTURE, "https://rss.donga.com/culture.xml",
            NewsCategory.SPORTS, "https://rss.donga.com/sports.xml"
    ));

    @Transactional
    public void collectAndSaveArticles() {
        rssFeeds.forEach((category, url) -> {
            try {
                SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
                int savedCount = 0;

                // TODO: 20개 제한 없이 모든 새로운 기사를 수집하도록 변경
                for (SyndEntry entry : feed.getEntries()) {
                    if (savedCount >= 20) break;

                    String link = entry.getLink();
                    if (newsRepository.existsByUrl(link)) continue;

                    News article = parseArticle(entry, category);
                    if (article != null) {
                        News saved = newsRepository.save(article); // DB 저장
                        NewsDocument document = News.toDocument(saved); // ES용 변환
                        newsDocumentRepository.save(document); // Elasticsearch 저장
                        savedCount++;
                        Thread.sleep(500); // 부하 방지
                    }

                }
            } catch (Exception e) {
                System.err.println("[ERROR] " + category + " 수집 실패 → " + e.getMessage());
            }
        });
    }

    private String extractThumbnail(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            Element ogImage = doc.selectFirst("meta[property=og:image]");
            if (ogImage != null) {
                String content = ogImage.attr("content");
                if (content.contains("logo") || content.contains("CI")) return "default";
                return content;
            }

            Element img = doc.selectFirst("div.article_img img");
            if (img != null) return img.attr("src");

        } catch (Exception e) {
            System.err.println("이미지 추출 실패: " + url + " → " + e.getMessage());
        }
        return "default";
    }

    private String downloadImage(String imageUrl, String articleDate) {
        String saveDir = BASE_IMAGE_SAVE_DIR + articleDate;
        File dir = new File(saveDir);
        if (!dir.exists()) dir.mkdirs();

        if (imageUrl.equals("default")) return DEFAULT_IMAGE_PATH;

        try {
            String originalPath = new URL(imageUrl).getPath();
            String extension = originalPath.contains(".")
                    ? originalPath.substring(originalPath.lastIndexOf(".") + 1)
                    : "jpg";

            if (!extension.matches("(?i)jpg|jpeg|png|gif")) return DEFAULT_IMAGE_PATH;

            String uuid = UUID.randomUUID().toString();
            String newFilename = uuid + "." + extension;

            File output = new File(dir, newFilename);
            if (output.exists()) return output.getPath();

            try (var in = new URL(imageUrl).openStream();
                 var out = new FileOutputStream(output)) {
                in.transferTo(out);
            }

            return output.getPath();
        } catch (Exception e) {
            System.err.println("이미지 다운로드 실패: " + imageUrl + " → " + e.getMessage());
            return DEFAULT_IMAGE_PATH;
        }
    }


    private News parseArticle(SyndEntry entry, NewsCategory category) {
        try {
            String link = entry.getLink();
            Document doc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // 본문 추출
            String content = doc.select("section.news_view").text();
            if (content.isBlank()) content = "본문없음";

            // 기자명
            String reporter = doc.select("meta[property=dd:author]").attr("content");

            // 날짜
            LocalDate publishedDate = null;
            if (entry.getPublishedDate() != null) {
                publishedDate = entry.getPublishedDate()
                        .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            } else {
                publishedDate = LocalDate.now();
            }

            // 이미지 추출 + 저장
            String imageUrl = extractThumbnail(link);
            String savedImagePath = downloadImage(imageUrl, publishedDate.toString()); // 저장된 경로

            return News.builder()
                    .mediaCompany(NewsMediaCompany.DONGA)
                    .category(category)
                    .title(entry.getTitle())
                    .publishDate(publishedDate)
                    .url(link)
                    .content(content)
                    .author(reporter)
                    .imageUrl(savedImagePath) // 저장된 이미지 경로만 DB/ES에 저장
                    .build();

        } catch (Exception e) {
            System.err.println("기사 파싱 실패: " + entry.getLink() + " → " + e.getMessage());
            return null;
        }
    }

}