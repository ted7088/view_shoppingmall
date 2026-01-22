package com.example.view_shoppingmall.dto;

import com.example.view_shoppingmall.entity.Wishlist;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishlistItemDTO {

    private Long wishlistId;
    private Long productId;
    private String productName;
    private Integer price;
    private String imageUrl;
    private Integer stock;
    private String category;
    private LocalDateTime addedAt;

    public static WishlistItemDTO fromEntity(Wishlist wishlist) {
        WishlistItemDTO dto = new WishlistItemDTO();
        dto.setWishlistId(wishlist.getId());
        dto.setProductId(wishlist.getProduct().getId());
        dto.setProductName(wishlist.getProduct().getName());
        dto.setPrice(wishlist.getProduct().getPrice());
        dto.setImageUrl(wishlist.getProduct().getImageUrl());
        dto.setStock(wishlist.getProduct().getStock());
        dto.setCategory(wishlist.getProduct().getCategory());
        dto.setAddedAt(wishlist.getCreatedAt());
        return dto;
    }
}
