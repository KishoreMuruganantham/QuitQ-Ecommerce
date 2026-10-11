package com.springboot.quitq.dto.response;

import com.springboot.quitq.enums.PaymentMethod;
import com.springboot.quitq.enums.PaymentStatus;

public record PaymentRespDto(
        Long id,
        String orderNumber,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String transactionId,
        Double amount
) {
}
