package com.tjeoun.newssearch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminJobUpdateDto {
    private Long id;
    private Boolean isResolved;
}
