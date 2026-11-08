package com.springboot.quitq.dto.response;

public record ResetLinkRespDto(
        String message,
        String resetToken
) {
}
