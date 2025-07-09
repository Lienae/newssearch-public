package com.tjeoun.newssearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  private String uploadDir;

  @Value("${upload.dir}")
  public void setUploadDir(String uploadDir) {
    this.uploadDir = uploadDir;
  }
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/images/upload/**")
      .addResourceLocations("file:///C:/workspace/newssearch/images/upload/");
  }
}
