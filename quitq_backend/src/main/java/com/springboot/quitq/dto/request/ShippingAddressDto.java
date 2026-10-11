package com.springboot.quitq.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ShippingAddressDto( //add validation annotations
        @NotBlank(message = "Recipient name is required")
        String recipientName,

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Street address is required")
        String streetAddress,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "State is required")
        String state,

        @NotBlank(message = "Postal code is required")
        String postalCode,

        boolean isDefault
) {
}
