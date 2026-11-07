package com.springboot.quitq.dto.response;

public record SalesReportRespDto(
        String productName,
        Long quantitySold,
        Double totalRevenue
) {
}
