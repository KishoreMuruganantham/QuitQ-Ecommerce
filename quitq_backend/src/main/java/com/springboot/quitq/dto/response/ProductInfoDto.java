package com.springboot.quitq.dto.response;

import com.springboot.quitq.enums.ProductStatus;

public record ProductInfoDto(
        Long id,
        String name,
        String brand,
        Double price,
        Integer stockQty,
        String categoryName,
        String sellerName,
        String imageUrl,
        String description,
        ProductStatus status,
        Long categoryId
) {
}
