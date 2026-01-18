package com.example.view_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO {
    private Long id;
    private Long questionId;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
}
