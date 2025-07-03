package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/boarders")
@RequiredArgsConstructor
public class AdminBoardController {

  private final BoardService boardService;

  @GetMapping("/list")
  public String list(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(defaultValue = "ALL") String category,
                     Model model) {

    Page<AdminBoardDto> boardPage = boardService.getBoards(page, size, category);

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
    AdminBoardDto dto = boardService.getBoardDto(id);

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
                     @ModelAttribute AdminBoardDto dto) {
    boardService.updateBoard(id, dto);
    return "redirect:/admin/boarders/list?page=" + page + "&size=" + size + "&category=" + category;
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    boardService.softDeleteBoard(id);
    return "redirect:/admin/boarders/list";
  }
}
