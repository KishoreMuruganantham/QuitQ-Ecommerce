package com.springboot.quitq.dto.response;

public record CustomerRespDto(
        Long id,
        String name,
        String gender,
        String contactNumber,
        String username,
        String email
) {
}
