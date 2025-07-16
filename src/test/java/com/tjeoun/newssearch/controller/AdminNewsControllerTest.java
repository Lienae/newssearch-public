package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.config.MockNewsFactory;
import com.tjeoun.newssearch.config.principal.PrincipalDetailsService;
import com.tjeoun.newssearch.entity.News;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminNewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockNewsFactory mockNewsFactory;

    @Autowired
    private PrincipalDetailsService principalDetailsService;
    @BeforeEach
    void setUp() {
        UserDetails admin = principalDetailsService.loadUserByUsername("elixirel.chrome@gmail.com");

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(admin, admin.getPassword(), admin.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    @Test
    @DisplayName("뉴스 목록 조회")
    void testList() throws Exception {
        // when
        mockMvc.perform(get("/admin/news/list")
                        .param("page", "0")
                        .param("size", "10")
                        .param("category", "ALL")
                        .param("mediaCompany", "ALL"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/news-list"))
                .andExpect(model().attributeExists("newsPage"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("size"))
                .andExpect(model().attributeExists("currentCategory"))
                .andExpect(model().attributeExists("currentMediaCompany"))
                .andExpect(model().attributeExists("totalCount"));
    }

    @Test
    @DisplayName("뉴스 수정 폼 조회")
    void testEditForm() throws Exception {
        // given
        News news = mockNewsFactory.createNews();

        // when
        mockMvc.perform(get("/admin/news/edit")
                        .param("id", news.getId().toString())
                        .param("page", "0")
                        .param("size", "10")
                        .param("category", "ALL")
                        .param("mediaCompany", "ALL"))
        // then
                .andExpect(status().isOk())
                .andExpect(view().name("admin/news-edit"))
                .andExpect(model().attributeExists("news"))
                .andExpect(model().attribute("page", 0))
                .andExpect(model().attribute("size", 10))
                .andExpect(model().attribute("currentCategory", "ALL"))
                .andExpect(model().attribute("currentMediaCompany", "ALL"));
    }

//    @Test
//    @DisplayName("뉴스 수정 처리")
//    void testEdit() throws Exception {
//        // given
//        News news = mockNewsFactory.createNews();
//
//        // when
//        mockMvc.perform(post("/admin/news/edit/" + news.getId())
//                        .with(csrf())
//                        .param("page", "0")
//                        .param("size", "10")
//                        .param("title", "수정된 제목")
//                        .param("content", "수정된 내용")
//                        .param("imageUrl", "http://test.com/newimage.jpg")
//                        .param("url", "http://test.com/newurl")
//                        .param("category", "POLITICS")
//                        .param("mediaCompany", "YTN"))
//        // then
//                        .andExpect(status().is3xxRedirection())
//                        .andExpect(redirectedUrlPattern("/admin/news/list*"));
//        assertDoesNotThrow(() -> {
//            News updated = newsRepository.findById(news.getId()).orElseThrow();
//            assertEquals(updated.getTitle(), "수정된 제목");
//            assertEquals(updated.getContent(), "수정된 내용");
//            assertEquals(updated.getImageUrl(), "http://test.com/newimage.jpg");
//            assertEquals(updated.getUrl(), "http://test.com/newurl");
//            assertEquals(updated.getCategory(), NewsCategory.POLITICS);
//            assertEquals(updated.getMediaCompany(), NewsMediaCompany.YTN);
//        });
//    }
//
//    @Test
//    @DisplayName("뉴스 삭제 처리")
//    void testDelete() throws Exception {
//        News news = mockNewsFactory.createNews();
//        given(newsRepository.findById(news.getId())).willReturn(Optional.of(news));
//
//        mockMvc.perform(post("/admin/news/delete/1")
//                        .with(csrf()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/admin/news/list"));
//
//        then(newsRepository).should().save(news);
//    }


}
