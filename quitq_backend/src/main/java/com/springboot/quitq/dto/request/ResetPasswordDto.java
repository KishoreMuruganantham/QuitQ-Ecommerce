package com.springboot.quitq.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordDto(
        @NotBlank(message = "Reset token is required")
        String token,

        @NotBlank(message = "Password is required")
        String newPassword
) {
}
