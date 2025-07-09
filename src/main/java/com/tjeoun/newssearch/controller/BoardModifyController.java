package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.entity.AttachFile;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.enums.NewsCategory;
import com.tjeoun.newssearch.repository.AttachFileRepository;
import com.tjeoun.newssearch.service.BoardModifyService;
import com.tjeoun.newssearch.service.BoardService;
import com.tjeoun.newssearch.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/board/edit")
public class BoardModifyController {
  private final BoardModifyService boardModifyService;
  private final MemberRepository memberRepository;
  private final BoardService boardService;
  private final AttachFileRepository attachFileRepository;



  // 수정 페이지 열기(GET)
  @GetMapping("/{id}")
  public String showEditForm(@PathVariable Long id, Model model, Principal principal) {
    BoardDto boardDto = boardModifyService.getEditableBoard(id, principal);
    List<AttachFile> attachFiles = boardModifyService.listAttachmentsByBoardId(id);

    model.addAttribute("boardDto", boardDto);
    model.addAttribute("categories", NewsCategory.values());
    model.addAttribute("attachFiles", attachFiles);

    return "board/board-modify";
  }

  // 수정 처리(POST)
  @PostMapping("/{id}")
  public String updateBoard(@PathVariable Long id,
                            @ModelAttribute BoardDto boardDto,
                            @RequestParam(value = "files", required = false) MultipartFile[] files,
                            @RequestParam(value = "deleteFileIds", required = false) List<Long> deleteFileIds,
                            Principal principal) {
    boardModifyService.editBoardWithAttachments(id, boardDto, files, deleteFileIds, principal);
    return "redirect:/board/detail/" + id;
  }


  // 게시글 숨기기 (is_blind 처리)
  @PostMapping("/delete/{id}")
  public String blindBoard(@PathVariable Long id, Principal principal) {
    boardModifyService.blindBoard(id, principal);
    return "redirect:/board/list";
  }

}