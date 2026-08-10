package com.digipals.wms.goodsreceiving.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateGoodsReceiptLineRequest {

    @NotNull(message = "Goods Receipt is required")
    private UUID goodsReceiptId;

    @NotNull(message = "Purchase Order Line is required")
    private UUID purchaseOrderLineId;

    @NotNull(message = "Received quantity is required")
    @DecimalMin(value = "0.00", message = "Received quantity cannot be negative")
    private BigDecimal receivedQuantity;

    @NotNull(message = "Accepted quantity is required")
    @DecimalMin(value = "0.00", message = "Accepted quantity cannot be negative")
    private BigDecimal acceptedQuantity;

    @NotNull(message = "Rejected quantity is required")
    @DecimalMin(value = "0.00", message = "Rejected quantity cannot be negative")
    private BigDecimal rejectedQuantity;

    private String remarks;
}
