package com.tjeoun.newssearch.service;

import com.tjeoun.newssearch.dto.NewsDto;
import com.tjeoun.newssearch.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tjeoun.newssearch.dto.AdminNewsDto;
import com.tjeoun.newssearch.entity.News;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.enums.NewsMediaCompany;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminNewsService {

    private final NewsRepository newsRepository;
    private final AttachFileService attachFileService;
    private final NewsSearchService newsSearchService;

    public Page<AdminNewsDto> getNewsPage(int page, int size, String category, String mediaCompany, String query) {
        Sort sort = Sort.by(Sort.Order.desc("publishDate"), Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<News> news;

        boolean isAllCategory = "ALL".equals(category);
        boolean isAllMedia = "ALL".equals(mediaCompany);

        // 검색어가 있을 때, 검색 조건 추가
        if (query != null && !query.isEmpty()) {
            // 기존의 뉴스 검색 서비스 활용
            Page<NewsDto> newsDtoPage = newsSearchService.search(query, category, mediaCompany, page, size);

            // newsDtoPage에 대한 id 출력 (디버깅)
            newsDtoPage.getContent().forEach(newsDto -> System.out.println("newsID : " + newsDto.getId()));

            return newsDtoPage.map(AdminNewsDto::fromNewsDto);
        } else {
            // 검색어가 없을 경우 기존 로직 그대로 처리
            if (isAllCategory && isAllMedia) {
                news = newsRepository.findAll(pageable);
            } else if (!isAllCategory && isAllMedia) {
                NewsCategory newsCategory = NewsCategory.valueOf(category);
                news = newsRepository.findByCategory(newsCategory, pageable);
            } else if (isAllCategory) {
                NewsMediaCompany newsMedia = NewsMediaCompany.valueOf(mediaCompany);
                news = newsRepository.findByMediaCompany(newsMedia, pageable);
            } else {
                NewsCategory newsCategory = NewsCategory.valueOf(category);
                NewsMediaCompany newsMedia = NewsMediaCompany.valueOf(mediaCompany);
                news = newsRepository.findByCategoryAndMediaCompany(newsCategory, newsMedia, pageable);
            }
        }

        // List<News>에 대해 ID 출력 (디버깅)
        news.getContent().forEach(newsItem -> System.out.println("news ID : " + newsItem.getId()));

        return news.map(AdminNewsDto::fromEntity);
    }



    public AdminNewsDto getNewsDto(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        return AdminNewsDto.fromEntity(news);
    }

    @Transactional
    public void updateNewsWithFile(Long id, AdminNewsDto dto, MultipartFile file) throws Exception {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setCategory(dto.getCategory());
        news.setMediaCompany(dto.getMediaCompany());
        news.setUrl(dto.getUrl());
        news.setBlind(Boolean.TRUE.equals(dto.getIsBlind()));

        if (news.getImageUrl() != null && file != null && !file.isEmpty()) {
            String oldFileName = extractFileNmaeFromUrl(news.getImageUrl());
            attachFileService.deleteFile(oldFileName);
        }

        if (file != null && !file.isEmpty()) {
            String serverFilename = attachFileService.saveFile(file.getOriginalFilename(), file.getBytes());
            String imageUrl = "/images/upload/" + serverFilename;
            news.setImageUrl(imageUrl);
        }
    }

    private String extractFileNmaeFromUrl(String imageUrl) {
        if (imageUrl == null) return null;
        int idx = imageUrl.lastIndexOf("/");
        return idx >= 0 ? imageUrl.substring(idx + 1) : imageUrl;
    }

    @Transactional
    public void softDeleteNews(Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        news.setBlind(true);
        newsRepository.save(news);
    }

    public List<AdminNewsDto> getRecentNewsList() {
        return newsRepository.findTop5ByOrderByIdDesc()
                .stream()
                .map(AdminNewsDto::fromEntity)
                .toList();
    }

    // PPT 캡처용
    //  public List<AdminNewsDto> getTestNewsList() {
    //    return newsRepository.findTop5ByMediaCompanyOrderByIdDesc(NewsMediaCompany.JOONGANG)
    //            .stream()
    //            .map(AdminNewsDto::fromEntity)
    //            .toList();
    //  }

}

