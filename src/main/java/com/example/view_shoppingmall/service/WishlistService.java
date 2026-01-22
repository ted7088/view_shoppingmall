package com.example.view_shoppingmall.service;

import com.example.view_shoppingmall.dto.WishlistItemDTO;
import com.example.view_shoppingmall.entity.Product;
import com.example.view_shoppingmall.entity.User;
import com.example.view_shoppingmall.entity.Wishlist;
import com.example.view_shoppingmall.repository.ProductRepository;
import com.example.view_shoppingmall.repository.UserRepository;
import com.example.view_shoppingmall.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /**
     * 찜하기 추가
     */
    @Transactional
    public WishlistItemDTO addToWishlist(Long userId, Long productId) {
        // 사용자와 상품 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 이미 찜한 상품인지 확인
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("이미 찜한 상품입니다.");
        }

        // 찜하기 생성
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);

        Wishlist saved = wishlistRepository.save(wishlist);
        return WishlistItemDTO.fromEntity(saved);
    }

    /**
     * 찜하기 삭제
     */
    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        if (!wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            throw new RuntimeException("찜 목록에 없는 상품입니다.");
        }

        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
    }

    /**
     * 사용자의 찜 목록 조회
     */
    @Transactional(readOnly = true)
    public List<WishlistItemDTO> getUserWishlist(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        return wishlists.stream()
                .map(WishlistItemDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 상품이 찜 목록에 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean isInWishlist(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }
}
