package com.tjeoun.newssearch.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;


@Entity
@Getter
@Table(name = "board_reply_count_view")
public class BoardReplyCountView {
  @Id
  private Long boardId;

  private Long replyCount;
}
