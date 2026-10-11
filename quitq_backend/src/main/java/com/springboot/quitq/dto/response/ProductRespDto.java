package com.springboot.quitq.dto.response;

import com.springboot.quitq.enums.ProductStatus;

public record ProductRespDto(
        Long id,
        String name,
        String brand,
        Double price,
        Integer stockQty,
        ProductStatus status
) {
}
