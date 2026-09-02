package com.digipals.wms.billing.dto;

import com.digipals.wms.billing.entity.BillingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BillingDocumentResponse(
        UUID id,
        String billingNumber,
        UUID outboundDeliveryId,
        String customerCode,
        String billingType,
        String currency,
        BillingStatus status,
        LocalDateTime billingDate,
        BigDecimal totalAmount,
        String remarks,
        List<BillingDocumentItemResponse> items
) {
}
