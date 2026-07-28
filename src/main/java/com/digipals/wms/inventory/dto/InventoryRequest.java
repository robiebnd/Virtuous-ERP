package com.digipals.wms.inventory.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InventoryRequest {

    private UUID warehouseId;

    private UUID productId;

    private BigDecimal quantityOnHand;

    private BigDecimal quantityReserved;

    private BigDecimal reorderLevel;
}