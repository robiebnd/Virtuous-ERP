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

    @NotNull(message = "Product is required")
    private UUID productId;

    @NotNull(message = "Received quantity is required")
    @DecimalMin(value = "0.01")
    private BigDecimal receivedQuantity;

    @NotNull(message = "Accepted quantity is required")
    @DecimalMin(value = "0.00")
    private BigDecimal acceptedQuantity;

    @NotNull(message = "Rejected quantity is required")
    @DecimalMin(value = "0.00")
    private BigDecimal rejectedQuantity;

    @NotNull(message = "Unit cost is required")
    @DecimalMin(value = "0.00")
    private BigDecimal unitCost;

    private String remarks;
}