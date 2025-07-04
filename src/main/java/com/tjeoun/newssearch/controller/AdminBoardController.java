package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.AdminAttachFileDto;
import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.service.AdminBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
    List<AdminAttachFileDto> files = adminBoardService.getAttachFiles(id);

    model.addAttribute("board", dto);
    model.addAttribute("files", files != null ? files : List.of());
    model.addAttribute("page", page);
    model.addAttribute("size", size);
    model.addAttribute("currentCategory", category);

    return "admin/boarder-edit";
  }

  @PostMapping("/edit/{id}")
  public String edit(@PathVariable Long id,
                     @RequestParam("page") int page,
                     @RequestParam("size") int size,
                     @RequestParam("filterCategory") String currentCategory,
                     @ModelAttribute AdminBoardDto dto,
                     @RequestParam(value = "files", required = false) List<MultipartFile> files,
                     RedirectAttributes redirectAttributes) {
    try {
      adminBoardService.updateBoardWithFiles(id, dto, files);
    } catch (Exception e) {
      log.error("게시글 수정 실패", e);
      redirectAttributes.addFlashAttribute("errorMessage", "게시글 수정 실패: " + e.getMessage());
    }
    return "redirect:/admin/boarders/list?page=" + page + "&size=" + size + "&category=" + currentCategory + "&success=update";
  }



  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    adminBoardService.softDeleteBoard(id);
    return "redirect:/admin/boarders/list?success=delete";
  }
}
