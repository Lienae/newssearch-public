package com.tjeoun.newssearch.helper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class HaniCrawlerHelper {
    public static String haniExtractReporterFromDiv (Document doc){
        Element reporterDiv = doc.selectFirst("div.ArticleDetailView_reporterList__waOKp");
        if (reporterDiv == null) {
            return "";
        }

        Elements reporterLinks = reporterDiv.select("a.ArticleDetailView_reporterLink__UzTVy");
        List<String> names = new ArrayList<>();
        for (Element a : reporterLinks) {
            String name = a.text().replace(",", "").trim();
            if (!name.isEmpty()) {
                names.add(name);
            }
        }

        return String.join(",", names);
    }
}
