package com.digipals.wms.inventory.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class InventoryResponse {

    private UUID id;

    private String warehouseCode;

    private String warehouseName;

    private String productSku;

    private String productName;

    private BigDecimal quantityOnHand;

    private BigDecimal quantityReserved;

    private BigDecimal reorderLevel;
}
