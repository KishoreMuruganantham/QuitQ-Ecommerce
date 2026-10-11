package com.springboot.quitq.dto.response;

public record ProductInfoDto(
        Long id,
        String name,
        String brand,
        Double price,
        Integer stockQty,
        String categoryName,
        String sellerName,
        String imageUrl
) {
}
