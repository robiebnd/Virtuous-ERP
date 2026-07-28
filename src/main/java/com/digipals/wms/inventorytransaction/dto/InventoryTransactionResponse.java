package com.digipals.wms.inventorytransaction.dto;

import com.digipals.wms.inventorytransaction.entity.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class InventoryTransactionResponse {

    private UUID id;

    private UUID inventoryId;

    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private UUID productId;

    private String sku;

    private String productName;

    private TransactionType transactionType;

    private BigDecimal quantity;

    private BigDecimal balanceAfter;

    private String referenceNumber;

    private String referenceType;

    private UUID performedById;

    private String performedBy;

    private String remarks;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}