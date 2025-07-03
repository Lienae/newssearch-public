package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.service.AdminBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Controller
@RequestMapping("/admin/boarders")
@RequiredArgsConstructor
public class AdminBoardController {

  private final AdminBoardService adminBoardService;

  @GetMapping("/list")
  public String list(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(defaultValue = "ALL") String category,
                     Model model) {

    Page<AdminBoardDto> boardPage = adminBoardService.getBoards(page, size, category);

    long totalCount = boardPage.getTotalElements();

    model.addAttribute("boardPage", boardPage);
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    model.addAttribute("currentCategory", category);
    model.addAttribute("totalCount", totalCount);
    return "admin/boarder-list";
  }


  @GetMapping("/edit")
  public String editForm(@RequestParam Long id,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "10") int size,
                         @RequestParam(defaultValue = "ALL") String category,
                         Model model) {
    AdminBoardDto dto = adminBoardService.getBoardDto(id);

    model.addAttribute("board", dto);
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    model.addAttribute("currentCategory", category);

    return "admin/boarder-edit";
  }

  @PostMapping("/edit/{id}")
  public String edit(@PathVariable Long id,
                     @RequestParam int page,
                     @RequestParam int size,
                     @RequestParam String category,
                     //@ModelAttribute AdminBoardDto dto,
                     @RequestParam(value = "file", required = false) MultipartFile file) {
    if (file != null && !file.isEmpty()) {
      log.info("파일 이름: {}", file.getOriginalFilename());
      log.info("파일 크기: {} bytes", file.getSize());
      log.info("Content-Type: {}", file.getContentType());
    } else {
      log.info("파일이 첨부되지 않았습니다.");
    }
    //adminBoardService.updateBoard(id, dto);
    return "redirect:/admin/boarders/list?page=" + page + "&size=" + size + "&category=" + category;
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    adminBoardService.softDeleteBoard(id);
    return "redirect:/admin/boarders/list";
  }
}
