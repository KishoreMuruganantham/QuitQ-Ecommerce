package com.springboot.quitq.dto.response;

import com.springboot.quitq.enums.OrderStatus;

public record OrderStatusStatDto(
        OrderStatus orderStatus,
        Long numberOfOrders
) {
}
