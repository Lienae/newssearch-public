package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.document.BoardDocument;
import com.tjeoun.newssearch.dto.BoardDto;
import com.tjeoun.newssearch.enums.NewsCategory;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

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


        System.out.println("DEBUG: author = " + dto.getAuthor());
        System.out.println("DEBUG: author pw = " + dto.getAuthor().getPassword());
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


    // Board.java 파일 내 toDocument 메서드
    public static BoardDocument toDocument(Board board) {
        System.out.println("board.getCreatedDate() (toDocument 내부, 원본) = " + board.getCreatedDate());
        BoardDocument doc = new BoardDocument();
        doc.setId(String.valueOf(board.getId()));
        doc.setTitle(board.getTitle());
        doc.setContent(board.getContent());
        doc.setWriter(board.getAuthor().getName());
        // BoardService에서 사용되는 포맷터와 동일하게 명시적으로 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        if (board.getCreatedDate() != null) {
            LocalDateTime createdDateWithoutNanos = board.getCreatedDate().truncatedTo(ChronoUnit.SECONDS);
            doc.setCreatedDate(createdDateWithoutNanos.format(formatter));
        } else {
            doc.setCreatedDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).format(formatter));
        }

        System.out.println("BoardDocument createdDate (toDocument 내부, 밀리초 제거 후) = " + doc.getCreatedDate());

        doc.setNewsCategory(board.getNewsCategory());
        doc.setAdminArticle(board.isAdminArticle());
        doc.setBlind(board.isBlind());

        return doc;
    }

}
