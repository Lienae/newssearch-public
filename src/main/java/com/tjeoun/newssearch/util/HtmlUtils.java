package com.tjeoun.newssearch.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlUtils {

    public static String abbreviateHtml(String html, int maxLength) {
        if (html == null) return "";

        // 순수 텍스트만 추출
        String plainText = Jsoup.parse(html).text();

        if (plainText.length() <= maxLength) {
            return html; // 길이 짧으면 원본 반환
        }

        // 잘라낸 텍스트 기준으로 다시 HTML 구성
        String shortenedText = plainText.substring(0, maxLength) + "...";
        Document doc = Jsoup.parseBodyFragment(shortenedText);
        return doc.body().html(); // HTML-safe한 요약
    }

    public static String escapeAndConvertHighlight(String text) {
        if (text == null) return "";
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replaceAll("&lt;b&gt;", "<span class='highlight'>")
            .replaceAll("&lt;/b&gt;", "</span>");
    }
}

