package com.example.view_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String content;
    private Boolean isAnswered;
    private LocalDateTime createdAt;
    private AnswerDTO answer;
}
