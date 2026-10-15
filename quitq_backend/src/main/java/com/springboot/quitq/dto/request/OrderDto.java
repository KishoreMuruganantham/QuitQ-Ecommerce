package com.springboot.quitq.dto.request;

import com.springboot.quitq.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record OrderDto(
        @NotNull(message = "Customer id is required")
        Long customerId,

        @NotNull(message = "Shipping address id is required")
        Long shippingAddressId,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod
) {
}
