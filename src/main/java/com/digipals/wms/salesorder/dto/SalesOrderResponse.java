package com.digipals.wms.salesorder.dto;

import com.digipals.wms.salesorder.entity.SalesOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SalesOrderResponse(
        UUID id,
        String orderNumber,
        String sapOrderNumber,
        String customerCode,
        String salesOrganization,
        String distributionChannel,
        String division,
        SalesOrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime orderDate,
        String remarks,
        List<SalesOrderItemResponse> items
) {
}
