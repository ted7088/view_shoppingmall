package com.example.view_shoppingmall.controller;

import com.example.view_shoppingmall.dto.MessageResponse;
import com.example.view_shoppingmall.dto.WishlistItemDTO;
import com.example.view_shoppingmall.entity.User;
import com.example.view_shoppingmall.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * 찜하기 추가
     */
    @PostMapping("/{productId}")
    public ResponseEntity<?> addToWishlist(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal User user) {

        try {
            WishlistItemDTO wishlistItem = wishlistService.addToWishlist(user.getId(), productId);
            return ResponseEntity.ok(wishlistItem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * 찜하기 삭제
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> removeFromWishlist(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal User user) {

        try {
            wishlistService.removeFromWishlist(user.getId(), productId);
            return ResponseEntity.ok(new MessageResponse("찜 목록에서 삭제되었습니다."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * 사용자의 찜 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<WishlistItemDTO>> getUserWishlist(
            @AuthenticationPrincipal User user) {

        List<WishlistItemDTO> wishlist = wishlistService.getUserWishlist(user.getId());
        return ResponseEntity.ok(wishlist);
    }

    /**
     * 상품이 찜 목록에 있는지 확인
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkWishlistStatus(
            @PathVariable("productId") Long productId,
            @AuthenticationPrincipal User user) {

        boolean isWishlisted = wishlistService.isInWishlist(user.getId(), productId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isWishlisted", isWishlisted);
        return ResponseEntity.ok(response);
    }
}
