package com.digipals.wms.inventorytransaction.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InventoryTransactionRequest {

    private UUID inventoryId;

    private String transactionType;

    private BigDecimal quantity;

    private String referenceNumber;

    private String remarks;
}