package com.springboot.quitq.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductDto( //add validation annotations
        @NotBlank(message = "Name is required")
        String name,

        String brand,

        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        Double price,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        Integer stockQty,

        String imageUrl,

        Long categoryId
) {
}
