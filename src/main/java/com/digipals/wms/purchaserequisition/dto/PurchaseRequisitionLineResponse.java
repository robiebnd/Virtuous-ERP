package com.digipals.wms.purchaserequisition.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PurchaseRequisitionLineResponse {

    private UUID id;

    private UUID productId;

    private String sku;

    private String productName;

    private BigDecimal quantity;

    private BigDecimal estimatedUnitCost;

    private String remarks;
}