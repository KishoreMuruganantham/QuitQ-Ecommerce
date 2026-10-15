package com.springboot.quitq.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryDto(
        @NotBlank(message = "Name is required")
        String name,

        String description,

        Integer seq
) {
}
