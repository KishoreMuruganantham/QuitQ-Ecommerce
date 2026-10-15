package com.springboot.quitq.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CustomerDto(
        @NotBlank(message = "Name is required")
        String name,

        String gender,

        @NotBlank(message = "Contact number is required")
        String contactNumber,

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
