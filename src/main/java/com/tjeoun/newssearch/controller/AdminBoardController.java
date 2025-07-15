package com.tjeoun.newssearch.controller;

import com.tjeoun.newssearch.config.principal.PrincipalDetails;
import com.tjeoun.newssearch.dto.AdminAttachFileDto;
import com.tjeoun.newssearch.dto.AdminBoardDto;
import com.tjeoun.newssearch.entity.Board;
import com.tjeoun.newssearch.entity.Member;
import com.tjeoun.newssearch.service.AdminBoardService;
import com.tjeoun.newssearch.service.BoardSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller
@RequestMapping("/admin/boards")
@RequiredArgsConstructor
public class AdminBoardController {

    private final AdminBoardService adminBoardService;
    private final BoardSearchService boardSearchService;

    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(defaultValue = "ALL") String category,
                       @RequestParam(required = false) String filter,
                       @RequestParam(value = "searchType", defaultValue = "all") String searchType,
                       @AuthenticationPrincipal PrincipalDetails principalDetails,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        // 로그인 사용자 정보 가져오기 (getDefaultMember는 BoardSearchService로 옮겨야 합니다)
        Member loginUser = principalDetails.getMember();

        // Page<AdminBoardDto> boardPage = adminBoardService.getBoards(page, size, category);
        Page<Board> boardPage = boardSearchService.getFilteredAndSearchedBoards(keyword, searchType, pageable, category, filter, loginUser);
        Page<AdminBoardDto> dtoPage = boardPage.map(AdminBoardDto::fromEntity);

        long totalCount = boardPage.getTotalElements();

        model.addAttribute("boardPage", dtoPage);
        model.addAttribute("page", page);
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchType", searchType);
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
                       Model model) {
        try {
            adminBoardService.updateBoardWithFiles(id, dto, files);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "게시글 수정 실패: " + e.getMessage());

            return "error/error";
        }

        return "redirect:/admin/boards/list?page=" + page + "&size=" + size + "&category=" + currentCategory + "&success=update";
    }



    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        adminBoardService.softDeleteBoard(id);
        return "redirect:/admin/boards/list?success=delete";
    }
}
