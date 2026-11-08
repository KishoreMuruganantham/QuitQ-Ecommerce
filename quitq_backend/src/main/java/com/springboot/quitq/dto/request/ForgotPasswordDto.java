package com.springboot.quitq.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDto(
        @NotBlank(message = "Username is required")
        String username
) {
}
