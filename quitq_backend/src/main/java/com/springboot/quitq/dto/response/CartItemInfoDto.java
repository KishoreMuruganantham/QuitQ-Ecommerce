package com.springboot.quitq.dto.response;

public record CartItemInfoDto(
        Long cartItemId,
        Long productId,
        String productName,
        Integer quantity,
        Double price,
        Double lineTotal
) {
}
