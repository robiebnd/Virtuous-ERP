package com.digipals.wms.goodsreceiving.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class GoodsReceiptLineResponse {

    private UUID id;

    private UUID purchaseOrderLineId;

    private UUID productId;

    private String sku;

    private String productName;

    /**
     * Quantity from the Purchase Order that is still represented by this GRN line.
     * For a newly loaded line this is the current PO outstanding quantity.
     */
    private BigDecimal orderedQuantity;

    /**
     * Quantity already received/accepted against the PO before this GRN.
     */
    private BigDecimal previouslyReceivedQuantity;

    /**
     * Current authoritative outstanding quantity on the linked PO line.
     */
    private BigDecimal outstandingQuantity;

    private BigDecimal receivedQuantity;

    private BigDecimal acceptedQuantity;

    private BigDecimal rejectedQuantity;

    private BigDecimal unitCost;

    private String remarks;
}