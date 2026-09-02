package com.digipals.wms.outbounddelivery.dto;

import com.digipals.wms.outbounddelivery.entity.OutboundDeliveryStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OutboundDeliveryResponse(
        UUID id,
        String deliveryNumber,
        UUID salesOrderId,
        String salesOrderNumber,
        String customerCode,
        String shippingPoint,
        LocalDateTime requestedDeliveryDate,
        OutboundDeliveryStatus status,
        LocalDateTime pickedAt,
        LocalDateTime packedAt,
        LocalDateTime goodsIssueAt,
        List<Item> items
) {
    public record Item(
            UUID id,
            Integer itemNumber,
            String materialCode,
            BigDecimal orderedQuantity,
            BigDecimal pickedQuantity,
            BigDecimal packedQuantity,
            BigDecimal deliveredQuantity
    ) {}
}
