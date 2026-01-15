package com.example.view_shoppingmall.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long productId;
    private Long userId;
    private String username;
    private Integer rating;
    private String content;
    private LocalDateTime createdAt;
}
