package com.springboot.quitq.dto.response;

public record SellerRespDto(
        Long id,
        String businessName,
        String contactNumber,
        String city,
        String address,
        String username,
        String email
) {
}
