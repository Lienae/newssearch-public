package com.tjeoun.newssearch.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

  @GetMapping("/main")
  public String home() {
    return "main";
  }

//  @GetMapping("/admin/main")
//  public String adminMain() {
//    return "admin/main";
//  }

  @GetMapping("/admin/boarder-list")
  public String adminBoarderList() {
    return "admin/boarder-list";
  }

  @GetMapping("/admin/boarder-edit")
  public String adminBoarderEdit() {
    return "admin/boarder-edit";
  }

  @GetMapping("/admin/news-list")
  public String adminNewsList() {
    return "admin/news-list";
  }

  @GetMapping("/admin/news-edit")
  public String adminNewsEdit() {
    return "admin/news-edit";
  }

  @GetMapping("/admin/user-list")
  public String adminUserList() {
    return "admin/user-list";
  }

  @GetMapping("/admin/user-edit")
  public String adminUserEdit() {
    return "admin/user-edit";
  }

  @GetMapping("/boarder/list")
  public String boarderListPage() {
    return "boarder/boarder-list";
  }

  @GetMapping("/boarder/write")
  public String boarderWrite() {
    return "boarder/boarder-write";
  }

  @GetMapping("/boarder/detail")
  public String boarderDetail() {
    return "boarder/boarder-detail";
  }

  @GetMapping("/news/list")
  public String newsList() {
    return "news/news-list";
  }

  @GetMapping("/error")
  public String error404() {
    return "error/error";
  }


  @GetMapping("/user/login")
  public String login() {
    return "user/login";
  }

  @GetMapping("/user/register")
  public String register() {
    return "user/register";
  }

  @GetMapping("/user/mypage")
  public String mypage() {
    return "user/mypage";
  }

  @GetMapping("/user/find-password")
  public String findPassword() {
    return "user/find-password";
  }

  @GetMapping("/user/find-password-sent")
  public String findPasswordSent() {
    return "user/find-password-sent";
  }

  @GetMapping("/user/delete-user")
  public String deleteUser() {
    return "user/delete-user";
  }


}
