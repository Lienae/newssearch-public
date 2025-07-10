package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.enums.NewsCategory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "longtext")
    private String content;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NewsCategory newsCategory;


    @Column(name = "is_blind", nullable = false)
    private boolean isBlind;

    public void setIsBlind(boolean isBlind) {
        this.isBlind = isBlind;
    }



    @Column(nullable = false)
    private boolean isAdminArticle;



    public static Board createBoard(BoardDto dto) {


        return Board.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(dto.getAuthor())
                .newsCategory(dto.getNewsCategory())
                .isAdminArticle(Boolean.TRUE.equals(dto.getIsAdminArticle()))
                .isBlind(false)
                .build();
    }


    public static BoardDocument toDocument(Board board) {
        System.out.println("board.getCreatedDate() (toDocument 내부, 원본) = " + board.getCreatedDate());
        BoardDocument doc = new BoardDocument();
        doc.setId(String.valueOf(board.getId()));
        doc.setTitle(board.getTitle());
        doc.setContent(board.getContent());
        doc.setWriter(board.getAuthor().getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        if (board.getCreatedDate() != null) {
            // createdDate에서 밀리초 부분을 잘라내고 (truncatedTo) -> String으로 포맷팅
            LocalDateTime createdDateWithoutNanos = board.getCreatedDate().truncatedTo(ChronoUnit.SECONDS);
            doc.setCreatedDate(createdDateWithoutNanos.format(formatter)); // ★ 이 부분을 수정합니다.
        } else {
            // null 처리 (예: 현재 시간으로 대체)
            doc.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(formatter)); // ★ 이 부분도 수정합니다.
        }

        System.out.println("BoardDocument createdDate (toDocument 내부, 밀리초 제거 후) = " + doc.getCreatedDate());

        return doc;
    }

}
