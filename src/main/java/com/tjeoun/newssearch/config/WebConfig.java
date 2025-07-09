package com.tjeoun.newssearch.config;

<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
=======
import com.tjeoun.newssearch.interceptor.AdminLogInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
>>>>>>> origin/master
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
<<<<<<< HEAD
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
=======
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
>>>>>>> origin/master
}
