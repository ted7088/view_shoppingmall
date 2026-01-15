package com.example.view_shoppingmall.service;

import com.example.view_shoppingmall.dto.ReviewDTO;
import com.example.view_shoppingmall.dto.ReviewRequestDTO;
import com.example.view_shoppingmall.entity.Product;
import com.example.view_shoppingmall.entity.Review;
import com.example.view_shoppingmall.entity.User;
import com.example.view_shoppingmall.repository.ProductRepository;
import com.example.view_shoppingmall.repository.ReviewRepository;
import com.example.view_shoppingmall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 상품별 리뷰 목록 조회
    public List<ReviewDTO> getReviewsByProductId(Long productId) {
        List<Review> reviews = reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
        return reviews.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 평균 별점 조회
    public Double getAverageRating(Long productId) {
        Double avgRating = reviewRepository.findAverageRatingByProductId(productId);
        return avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0;
    }

    // 리뷰 개수 조회
    public Long getReviewCount(Long productId) {
        return reviewRepository.countByProductId(productId);
    }

    // 리뷰 생성
    @Transactional
    public ReviewDTO createReview(ReviewRequestDTO requestDTO, Long userId) {
        // 이미 리뷰를 작성했는지 확인
        if (reviewRepository.existsByProductIdAndUserId(requestDTO.getProductId(), userId)) {
            throw new RuntimeException("이미 이 상품에 리뷰를 작성하셨습니다.");
        }

        // 별점 유효성 검사
        if (requestDTO.getRating() < 1 || requestDTO.getRating() > 5) {
            throw new RuntimeException("별점은 1~5 사이여야 합니다.");
        }

        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setRating(requestDTO.getRating());
        review.setContent(requestDTO.getContent());

        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    // 리뷰 삭제 (본인만 가능)
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    // Entity -> DTO 변환
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setProductId(review.getProduct().getId());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        dto.setRating(review.getRating());
        dto.setContent(review.getContent());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
