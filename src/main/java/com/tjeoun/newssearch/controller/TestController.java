package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.service.NewsCrawlerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class TestController {
    private final NewsCrawlerService newsCrawlerService;

    @GetMapping("/gethani")
    @ResponseBody
    public String gethani() {
        try {
            newsCrawlerService.getHaniArticles();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }

    }
    @GetMapping("/getjoongang")
    @ResponseBody
    public String getjoongang() {
        try {
            newsCrawlerService.getJoongangArticles();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }

    }
}
