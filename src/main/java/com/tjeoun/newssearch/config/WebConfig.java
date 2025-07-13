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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/upload/**")
                .addResourceLocations("file:///" + UPLOAD_DIR + "/");

        // /news_images/폴더 밑에 있는 기사 사진 불러오기 위해 추가 -> 동아일보 사진 저장이 잘못된 경로로 저장되고 있음 
        registry.addResourceHandler("/news_images/**")
                .addResourceLocations("file:///C:/news_images/");
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
