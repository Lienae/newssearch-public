package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.service.NewsCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.PrintWriter;
import java.io.StringWriter;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final NewsCrawlerService newsCrawlerService;
    @GetMapping("/test")
    public String test() {
        try {
            newsCrawlerService.getDongaArticles();
            return "test success!";
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            return sw.toString();
        }
    }

}
