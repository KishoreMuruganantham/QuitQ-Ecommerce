package com.springboot.quitq.dto.response;

import java.time.Instant;

public record OrderTrackingRespDto(
        Long id,
        String status,
        Instant statusTime,
        String remarks
) {
}
