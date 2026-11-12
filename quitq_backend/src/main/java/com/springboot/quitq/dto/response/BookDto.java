package com.springboot.quitq.dto.response;

public record BookDto(
        Long id,
        String title,
        String genre,
        Integer publishedYear,
        Double price,
        String authorName
) {
}
