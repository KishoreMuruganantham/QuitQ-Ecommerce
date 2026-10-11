package com.springboot.quitq.dto.response;

import com.springboot.quitq.enums.OrderStatus;

import java.time.Instant;

public record OrderRespDto(
        Long orderId,
        String orderNumber,
        Double totalAmount,
        OrderStatus orderStatus,
        Instant orderDate
) {
}
