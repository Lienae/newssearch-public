package com.tjeoun.newssearch.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.tjeoun.newssearch.interceptor.AdminLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AdminLogInterceptor adminLogInterceptor;
    @Value("${attachFileLocation}")
    private String UPLOAD_DIR;

    @Value("${news.images.base-path.hani}")
    private String haniBasePath;

    @Value("${news.images.base-path.joongang}")
    private String joongangBasePath;

    @Value("${news.images.base-path.khan}")
    private String khanBasePath;

    @Value("${news.images.base-path.ytn}")
    private String ytnBasePath;

    @Value("${news.images.base-path.donga}")
    private String dongaBasePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/upload/**")
                .addResourceLocations("file:///" + UPLOAD_DIR + "/");
        registry.addResourceHandler("/news_images/hani/**")
                .addResourceLocations(("file:///" + haniBasePath + "/"));
        registry.addResourceHandler("/news_images/donga/**")
                .addResourceLocations(("file:///" + dongaBasePath + "/"));
        registry.addResourceHandler("/news_images/ytn/**")
                .addResourceLocations(("file:///" + ytnBasePath + "/"));
        registry.addResourceHandler("/news_images/khan/**")
                .addResourceLocations(("file:///" + khanBasePath + "/"));
        registry.addResourceHandler("/news_images/joongang/**")
                .addResourceLocations(("file:///" + joongangBasePath + "/"));

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminLogInterceptor)

                .addPathPatterns("/**");
    }

}
//
//  @Override
//  public void addResourceHandlers(ResourceHandlerRegistry registry) {
//    registry.addResourceHandler("/images/upload/**")
//      .addResourceLocations("file:///C:/workspace/newssearch/images/upload/");
//  }
