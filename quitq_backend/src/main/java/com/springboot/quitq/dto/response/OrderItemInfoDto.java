package com.springboot.quitq.dto.response;

import com.springboot.quitq.enums.OrderStatus;

public record OrderItemInfoDto(
        Long id,
        String orderNumber,
        String customerName,
        String productName,
        Integer quantity,
        Double totalPrice,
        OrderStatus status
) {
}
