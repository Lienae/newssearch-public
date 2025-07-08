package com.tjeoun.newssearch.helper;

import com.tjeoun.newssearch.util.NewsCrawlUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

import static com.tjeoun.newssearch.util.NewsCrawlUtils.*;

public class HaniCrawlerHelper {
    public static List<Map<String, String>> getArticles() {
        List<Map<String, String>> articles = new ArrayList<>();
        for(Map.Entry<String, String> entry : HANI_RSS_MAP.entrySet()) {
            String category = entry.getKey();
            String feedUrl = entry.getValue();
            try {
                articles.addAll(parseAndCollect(feedUrl, category));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return articles;
    }
    public static List<Map<String, String>> parseAndCollect(String feedUrl, String category) throws IOException {
        Document rssDoc = Jsoup.connect(feedUrl)
                .userAgent(NewsCrawlUtils.USER_AGENT)
                .referrer("https://www.hani.co.kr/")
                .header("Accept", "application/rss+xml, application/xml;q=0.9, */*;q=0.8")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .timeout(10000)
                .get();
        Elements items = rssDoc.select("item");
        return parseItems(items, category);
    }
    private static List<Map<String, String>> parseItems(Elements elements, String category) {
        List<Map<String, String>> articles = new ArrayList<>();
        for (Element item : elements) {
            String title = Objects.requireNonNull(item.selectFirst("title")).text();
            String articleUrl = Objects.requireNonNull(item.selectFirst("link")).text();
            try {
                Document doc = Jsoup.connect(articleUrl)
                        .userAgent(USER_AGENT)
                        .referrer("https://www.hani.co.kr/")
                        .timeout(10000)
                        .get();

                String pubDate = parsePubDate(doc.selectFirst("li.ArticleDetailView_dateListItem__mRc3d > span"));
                String content = parseContent(doc.selectFirst("div.article-text"));
                String reporter = extractReporter(doc);
                String savedImagePath = downloadImageAndReturnUrl(doc.selectFirst("picture > source[type=image/jpeg]"));

                articles.add(createNewsMap("한겨레", category, title, content, reporter, pubDate, articleUrl, savedImagePath));

                Thread.sleep(500);  // 서버 부하 방지

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return articles;
    }
    private static String parsePubDate(Element pubDateElement) {
        return pubDateElement != null
                ? pubDateElement.text().replaceAll(" [0-9]{2}:[0-9]{2}", "")
                : "";
    }
    private static String parseContent(Element articleDiv) {
        StringBuilder content = new StringBuilder();
        if (articleDiv != null) {
            for (Element p : articleDiv.select("p.text")) {
                content.append(p.text()).append("\n");
            }
        }
        return content.toString().trim();
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

    private static String downloadImageAndReturnUrl(Element picture) {
        String imageUrl;
        String savedImagePath;
        if (picture != null) {
            imageUrl = picture.attr("srcset");
            savedImagePath = NewsCrawlUtils.downloadImage(imageUrl, haniImageBasePath);
        } else {
            savedImagePath = defaultImageBasePath;
        }
        return savedImagePath;
    }
}
