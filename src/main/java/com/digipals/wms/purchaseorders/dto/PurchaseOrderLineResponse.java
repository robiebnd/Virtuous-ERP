package com.digipals.wms.purchaseorders.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PurchaseOrderLineResponse {

    private UUID id;

    private UUID productId;

    private String sku;

    private String productName;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal discount;

    private BigDecimal tax;

    private BigDecimal lineTotal;

    private String remarks;
}