package com.springboot.quitq.dto.response;

import com.springboot.quitq.enums.Role;

public record UserRespDto(
        Long id,
        String username,
        String email,
        Role role
) {
}
