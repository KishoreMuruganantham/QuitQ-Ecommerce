package com.springboot.quitq.dto.response;

import java.util.List;

public record CartInfoDto(
        Long cartId,
        String customerName,
        List<CartItemInfoDto> items,
        double total
) {
}
