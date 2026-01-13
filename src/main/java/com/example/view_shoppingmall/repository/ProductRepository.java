package com.example.view_shoppingmall.repository;

import com.example.view_shoppingmall.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // 카테고리별 상품 조회
    List<Product> findByCategory(String category);

    // 상품명으로 검색 (부분 일치)
    List<Product> findByNameContaining(String name);

    // 가격 범위로 검색
    List<Product> findByPriceBetween(Integer minPrice, Integer maxPrice);

    // 재고가 있는 상품만 조회
    List<Product> findByStockGreaterThan(Integer stock);
}
