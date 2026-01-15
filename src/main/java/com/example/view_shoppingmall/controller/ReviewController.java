package com.example.view_shoppingmall.controller;

import com.example.view_shoppingmall.dto.ReviewDTO;
import com.example.view_shoppingmall.dto.ReviewRequestDTO;
import com.example.view_shoppingmall.entity.User;
import com.example.view_shoppingmall.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {

    private final ReviewService reviewService;

    // 상품별 리뷰 목록 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByProductId(@PathVariable("productId") Long productId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    // 상품별 평균 별점 및 리뷰 개수 조회
    @GetMapping("/product/{productId}/rating")
    public ResponseEntity<Map<String, Object>> getProductRating(@PathVariable("productId") Long productId) {
        Map<String, Object> result = new HashMap<>();
        result.put("averageRating", reviewService.getAverageRating(productId));
        result.put("reviewCount", reviewService.getReviewCount(productId));
        return ResponseEntity.ok(result);
    }

    // 리뷰 생성
    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestBody ReviewRequestDTO requestDTO,
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            ReviewDTO createdReview = reviewService.createReview(requestDTO, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요합니다."));
        }

        try {
            reviewService.deleteReview(id, user.getId());
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
