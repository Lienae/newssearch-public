package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.helper.HaniCrawlerHelper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.tjeoun.newssearch.helper.JoongangCrawlerHelper.getJoongangArticleLinks;
import static com.tjeoun.newssearch.helper.JoongangCrawlerHelper.parseArticle;

@Service
public class NewsCrawlerService {
    private final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";
    //@Value("${hani.images.base-path}")
    String haniImageBasePath = "f:/hani_images";

    //@Value("${joongang.images.base-path}")
    String joongangImageBasePath = "f:/joongang_images";

    public void getHaniArticles() {
        final Map<String, String> rssFeeds = Map.of(
                "정치", "https://www.hani.co.kr/rss/politics/",
                "경제", "https://www.hani.co.kr/rss/economy/",
                "사회", "https://www.hani.co.kr/rss/society/",
                "문화", "https://www.hani.co.kr/rss/culture/",
                "스포츠", "https://www.hani.co.kr/rss/sports/"
        );
        List<Map<String, String>> articles = new ArrayList<>();
        rssFeeds.forEach((category, feedUrl) -> {
            try {
                Document rssDoc = Jsoup.connect(feedUrl)
                        .userAgent(USER_AGENT)
                        .referrer("https://www.hani.co.kr/")
                        .header("Accept", "application/rss+xml, application/xml;q=0.9, */*;q=0.8")
                        .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                        .timeout(10000)
                        .get();

                Elements items = rssDoc.select("item");
                for (Element item : items.subList(0, Math.min(20, items.size()))) {
                    String title = Objects.requireNonNull(item.selectFirst("title")).text();
                    String articleUrl = Objects.requireNonNull(item.selectFirst("link")).text();
                    try {
                        Document doc = Jsoup.connect(articleUrl)
                                .userAgent(USER_AGENT)
                                .referrer("https://www.hani.co.kr/")
                                .timeout(10000)
                                .get();

                        Element pubDateElement = doc.selectFirst("li.ArticleDetailView_dateListItem__mRc3d > span");
                        String pubDate = pubDateElement != null
                                ? pubDateElement.text().replaceAll(" [0-9]{2}:[0-9]{2}", "")
                                : "";

                        Element articleDiv = doc.selectFirst("div.article-text");
                        StringBuilder content = new StringBuilder();


                        if (articleDiv != null) {
                            for (Element p : articleDiv.select("p.text")) {
                                content.append(p.text()).append("\n");
                            }
                        }

                        String reporter = HaniCrawlerHelper.haniExtractReporterFromDiv(doc);

                        Element picture = doc.selectFirst("picture > source[type=image/jpeg]");
                        String imageUrl;
                        String savedImagePath = "";
                        if (picture != null) {
                            imageUrl = picture.attr("srcset");
                            savedImagePath = downloadImage(imageUrl, haniImageBasePath);
                        }

                        Map<String, String> articleData = new LinkedHashMap<>();
                        articleData.put("언론사", "한겨레");
                        articleData.put("카테고리", category);
                        articleData.put("제목", title);
                        articleData.put("내용", content.toString().trim());
                        articleData.put("기자명", reporter);
                        articleData.put("날짜", pubDate);
                        articleData.put("링크", articleUrl);
                        articleData.put("대표이미지", savedImagePath);

                        articles.add(articleData);

                        Thread.sleep(500);  // 서버 부하 방지

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // todo: DB 입력으로 변환할 것
        saveToCsv(articles, "hani_articles.csv");
    }


    private void saveToCsv (List<Map<String, String>> articles, String filename){
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("언론사", "카테고리", "제목", "내용", "기자명", "날짜", "링크", "대표이미지").get())) {
            for (Map<String, String> article : articles) {
                csvPrinter.printRecord(
                        article.get("언론사"),
                        article.get("카테고리"),
                        article.get("제목"),
                        article.get("내용"),
                        article.get("기자명"),
                        article.get("날짜"),
                        article.get("링크"),
                        article.get("대표이미지")
                );
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getJoongangArticles() {
        final Map<String, String> CATEGORY_URLS = Map.of(
                "정치", "https://www.joongang.co.kr/politics/general",
                "경제", "https://www.joongang.co.kr/money/general",
                "문화", "https://www.joongang.co.kr/culture/general",
                "사회", "https://www.joongang.co.kr/society/general",
                "스포츠", "https://www.joongang.co.kr/sports/general"
        );


        List<Map<String, String>> articles = new ArrayList<>();

        for (Map.Entry<String, String> entry : CATEGORY_URLS.entrySet()) {
            List<String> links = getJoongangArticleLinks(USER_AGENT, entry.getValue());
            for (String link : links.stream().limit(20).toList()) {
                Map<String, String> article = parseArticle(USER_AGENT, link, joongangImageBasePath);
                if (article != null) {
                    article.put("카테고리", entry.getKey());
                    articles.add(article);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
        // todo: DB 입력으로 변환할 것
        saveToCsv(articles, "joongang_articles.csv");
    }

    public static String downloadImage (String imageUrl, String basePath){
        try {
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Path dirPath = Paths.get(basePath, dateStr);
            Files.createDirectories(dirPath);

            String extension = imageUrl.substring(imageUrl.lastIndexOf("."));
            String filename = UUID.randomUUID() + extension;
            Path imagePath = dirPath.resolve(filename);

            try (InputStream in = new URL(imageUrl).openStream()) {
                Files.copy(in, imagePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return imagePath.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }


}
