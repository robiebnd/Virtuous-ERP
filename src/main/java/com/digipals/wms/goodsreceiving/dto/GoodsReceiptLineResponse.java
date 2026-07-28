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

    private BigDecimal orderedQuantity;

    private BigDecimal receivedQuantity;

    private BigDecimal acceptedQuantity;

    private BigDecimal rejectedQuantity;

    private BigDecimal unitCost;

    private String remarks;
}