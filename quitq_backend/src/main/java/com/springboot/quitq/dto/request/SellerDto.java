package com.springboot.quitq.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SellerDto(
        @NotBlank(message = "Business name is required")
        String businessName,

        @NotBlank(message = "Contact number is required")
        String contactNumber,

        String city,

        String address,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
