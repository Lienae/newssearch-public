package com.tjeoun.newssearch.entity;

import com.tjeoun.newssearch.dto.EmoticonDto;
import com.tjeoun.newssearch.enums.EmoticonEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "news_id", "emoticon_enum"}))
public class Emoticon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Enumerated(EnumType.STRING)
    @Column(name = "emoticon_enum", nullable = false)
    private EmoticonEnum emoticonEnum;

    public static Emoticon makeEmoticon(EmoticonDto dto) {
        return Emoticon.builder()
                .member(dto.getMember())
                .news(dto.getNews())
                .emoticonEnum(dto.getEmoticonEnum())
                .build();
    }
}
