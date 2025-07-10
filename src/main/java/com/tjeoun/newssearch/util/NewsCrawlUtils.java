package com.tjeoun.newssearch.util;

import com.tjeoun.newssearch.document.NewsDocument;
import com.tjeoun.newssearch.dto.AdminJobDto;
import com.tjeoun.newssearch.entity.AdminJob;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.AdminJobsEnum;
import com.tjeoun.newssearch.repository.AdminJobRepository;
import com.tjeoun.newssearch.repository.NewsDocumentRepository;
import com.tjeoun.newssearch.repository.NewsRepository;
import com.tjeoun.newssearch.service.AdminJobService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NewsCrawlUtils {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";

    public static final Map<String, String> HANI_RSS_MAP = Map.of(
            "정치", "https://www.hani.co.kr/rss/politics/",
            "경제", "https://www.hani.co.kr/rss/economy/",
            "사회", "https://www.hani.co.kr/rss/society/",
            "문화", "https://www.hani.co.kr/rss/culture/",
            "스포츠", "https://www.hani.co.kr/rss/sports/"
    );

    public static final Map<String, String> JOONGANG_SITE_MAP = Map.of(
            "정치", "https://www.joongang.co.kr/politics/general",
            "경제", "https://www.joongang.co.kr/money/general",
            "문화", "https://www.joongang.co.kr/culture/general",
            "사회", "https://www.joongang.co.kr/society/general",
            "스포츠", "https://www.joongang.co.kr/sports/general"
    );
    public static final Map<String, String> KHAN_RSS_MAP = Map.of(
            "정치", "https://www.khan.co.kr/rss/rssdata/politic_news.xml",
            "경제", "https://www.khan.co.kr/rss/rssdata/economy_news.xml",
            "사회", "https://www.khan.co.kr/rss/rssdata/society_news.xml",
            "문화", "https://www.khan.co.kr/rss/rssdata/culture_news.xml",
            "스포츠", "https://www.khan.co.kr/rss/rssdata/kh_sports.xml"
    );
    public static final Map<String, String> DONGA_RSS_MAP = new LinkedHashMap<>(Map.of(
            "정치", "https://rss.donga.com/politics.xml",
            "경제", "https://rss.donga.com/economy.xml",
            "사회", "https://rss.donga.com/national.xml",
            "문화", "https://rss.donga.com/culture.xml",
            "스포츠", "https://rss.donga.com/sports.xml"
    ));
    // setters for static values
    @Value("${news.images.default-path}")
    public void setDefaultImageBasePath(String value) {
        NewsCrawlUtils.defaultImageBasePath = value;
    }

    @Value("${news.images.base-path.hani}")
    public void setHaniImageBasePath(String value) {
        NewsCrawlUtils.haniImageBasePath = value;
    }

    @Value("${news.images.base-path.joongang}")
    public void setJoongangImageBasePath(String value) {
        NewsCrawlUtils.joongangImageBasePath = value;
    }

    @Value("${news.images.base-path.khan}")
    public void setKhanImageBasePath(String value) {
        NewsCrawlUtils.khanImageBasePath = value;
    }

    @Value("${news.images.base-path.ytn}")
    public void setYtnImageBasePath(String value) {
        NewsCrawlUtils.ytnImageBasePath = value;
    }
    @Value("${news.images.base-path.donga}")
    public void setDongaImageBasePath(String value) {
        NewsCrawlUtils.dongaImageBasePath = value;
    }
    public static String defaultImageBasePath;
    public static String haniImageBasePath;
    public static String joongangImageBasePath;
    public static String khanImageBasePath;
    public static String ytnImageBasePath;
    public static String dongaImageBasePath;

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

    public static void saveToDatabase(List<Map<String, String>> articles,
                                      PlatformTransactionManager transactionManager,
                                      NewsRepository newsRepository,
                                      NewsDocumentRepository newsDocumentRepository,
                                      AdminJobRepository adminJobRepository) {
        for (Map<String, String> article : articles) {
            if(!newsRepository.existsByUrl(article.get("링크")))
                saveSingleArticleWithCompensation(
                        article,
                        transactionManager,
                        newsRepository,
                        newsDocumentRepository,
                        adminJobRepository);
        }
    }

    private static void saveSingleArticleWithCompensation(Map<String, String> article,
                                                   PlatformTransactionManager transactionManager,
                                                   NewsRepository newsRepository,
                                                   NewsDocumentRepository newsDocumentRepository,
                                                   AdminJobRepository adminJobRepository) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("saveSingleArticleTx");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        News news;
        try {
            news = News.createNewsFromMap(article);
            newsRepository.save(news);

            NewsDocument document = News.toDocument(news);
            newsDocumentRepository.save(document);

            transactionManager.commit(status);
            if(StringUtils.isBlank(news.getAuthor())) {
                adminJobRepository.save(
                        AdminJob.fromDto(
                                AdminJobDto.builder()
                                .job(AdminJobsEnum.NEWS)
                                .targetId(news.getId())
                                .build()
                        )
                );
            }
        } catch (Exception e) {
            transactionManager.rollback(status);
            System.err.println("저장 실패, 롤백 수행: " + article.get("title") + " / " + e.getMessage());
        }
    }

    public static Map<String, String> createNewsMap(String mediaCompany,
                                                    String category,
                                                    String title,
                                                    String content,
                                                    String author,
                                                    String date,
                                                    String link,
                                                    String imageLink) {
        Map<String, String> articleData = new LinkedHashMap<>();
        articleData.put("언론사", mediaCompany);
        articleData.put("카테고리", category);
        articleData.put("제목", title);
        articleData.put("내용", content);
        articleData.put("기자명", author);
        articleData.put("날짜", date);
        articleData.put("링크", link);
        articleData.put("대표이미지", imageLink);
        return articleData;
    }
}
