package com.digipals.wms.common.mapper;

import com.digipals.wms.purchaseorders.dto.PurchaseOrderLineResponse;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderLine;

public final class PurchaseOrderLineMapper {

    private PurchaseOrderLineMapper() {
    }

    public static PurchaseOrderLineResponse toResponse(
            PurchaseOrderLine line) {

        if (line == null) {
            return null;
        }

        return PurchaseOrderLineResponse.builder()

                .id(line.getId())

                .productId(line.getProduct().getId())

                .sku(line.getProduct().getSku())

                .productName(line.getProduct().getName())

                .quantity(line.getQuantity())

                .unitPrice(line.getUnitPrice())

                .lineTotal(line.getLineTotal())

                .build();
    }
}