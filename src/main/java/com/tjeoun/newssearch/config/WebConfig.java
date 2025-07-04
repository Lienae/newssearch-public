package com.tjeoun.newssearch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private static final String UPLOAD_DIR = "file:///C:/newssearch/images/upload/";

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/upload/**")
      .addResourceLocations(UPLOAD_DIR);
  }
}
