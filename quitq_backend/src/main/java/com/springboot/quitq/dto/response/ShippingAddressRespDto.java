package com.springboot.quitq.dto.response;

public record ShippingAddressRespDto(
        Long id,
        String recipientName,
        String phone,
        String streetAddress,
        String city,
        String state,
        String postalCode,
        boolean isDefault
) {
}
