package com.tjeoun.newssearch.config;

import com.tjeoun.newssearch.interceptor.AdminLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminLogInterceptor)
                .addPathPatterns("/**");
    }
}
