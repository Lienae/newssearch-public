package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/boarders")
@RequiredArgsConstructor
public class AdminBoardController {

  private final BoardRepository boardRepository;

  @GetMapping("/list")
  public String list(@RequestParam(defaultValue = "0") int page,
                     @RequestParam(defaultValue = "10") int size,
                     @RequestParam(defaultValue = "ALL") String category,
                     Model model) {

    Page<Board> boards;

    if ("ALL".equals(category)) {
      boards = boardRepository.findAll(PageRequest.of(page, size));
    } else {
      boards = boardRepository.findByNewsCategory(NewsCategory.valueOf(category), PageRequest.of(page, size));
    }

    Page<AdminBoardDto> boardPage = boards.map(board -> AdminBoardDto.builder()
      .id(board.getId())
      .title(board.getTitle())
      .authorId(board.getAuthor() != null ? board.getAuthor().getId() : null)
      .authorName(board.getAuthor() != null ? board.getAuthor().getName() : null)
      .newsCategory(board.getNewsCategory())
      .createdDate(board.getCreatedDate())
      .modifiedDate(board.getModifiedDate())
      .build());

    model.addAttribute("boardPage", boardPage);
    model.addAttribute("currentCategory", category);
    return "admin/boarder-list";
  }


  @GetMapping("/edit")
  public String editForm(@RequestParam Long id, Model model) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("board not found"));
    AdminBoardDto dto = AdminBoardDto.builder()
      .id(board.getId())
      .title(board.getTitle())
      .content(board.getContent())
      .authorId(board.getAuthor().getId())
      .authorName(board.getAuthor().getName())
      .newsCategory(board.getNewsCategory())
      .createdDate(board.getCreatedDate())
      .modifiedDate(board.getModifiedDate())
      .build();
    model.addAttribute("board", dto);
    return "admin/boarder-edit";
  }

  @PostMapping("/edit/{id}")
  public String edit(@PathVariable Long id,
                     @ModelAttribute Board formBoard) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("board not found"));

    board.setTitle(formBoard.getTitle());
    board.setContent(formBoard.getContent());
    board.setNewsCategory(formBoard.getNewsCategory());
    boardRepository.save(board);

    return "redirect:/admin/boarders/list";
  }

  @PostMapping("/delete/{id}")
  public String delete(@PathVariable Long id) {
    Board board = boardRepository.findById(id)
      .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
    board.set_blind(true);  // is_blind 컬럼 true로 (Soft delete)
    boardRepository.save(board);
    return "redirect:/admin/boarders/list";
  }
}
