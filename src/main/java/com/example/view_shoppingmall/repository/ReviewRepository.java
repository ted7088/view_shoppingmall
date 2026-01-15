package com.example.view_shoppingmall.repository;

import com.example.view_shoppingmall.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 상품별 리뷰 목록 조회 (최신순)
    List<Review> findByProductIdOrderByCreatedAtDesc(Long productId);

    // 상품별 평균 별점 조회
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    // 상품별 리뷰 개수 조회
    Long countByProductId(Long productId);

    // 특정 사용자가 특정 상품에 리뷰를 작성했는지 확인
    boolean existsByProductIdAndUserId(Long productId, Long userId);
}
