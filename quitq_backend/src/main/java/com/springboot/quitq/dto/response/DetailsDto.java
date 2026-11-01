package com.springboot.quitq.dto.response;

import com.springboot.quitq.enums.Role;

public record DetailsDto(
        Long id,
        String username,
        Boolean isActive,
        Role role
) {
}
