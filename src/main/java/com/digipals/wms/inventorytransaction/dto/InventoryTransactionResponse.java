package com.digipals.wms.inventorytransaction.dto;

import com.digipals.wms.inventorytransaction.entity.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionResponse {

    private UUID id;

    /*
     * Inventory Bin
     */
    private UUID inventoryBinId;

    /*
     * Warehouse
     */
    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    /*
     * Bin
     */
    private UUID binId;

    private String binCode;

    /*
     * Product
     */
    private UUID productId;

    private String sku;

    private String productName;

    /*
     * Transaction
     */
    private TransactionType transactionType;

    private BigDecimal quantity;

    private BigDecimal balanceAfter;

    private String referenceNumber;

    private String referenceType;

    /*
     * User
     */
    private UUID performedById;

    private String performedBy;

    /*
     * Movement
     */
    private UUID fromBinId;

    private String fromBinCode;

    private UUID toBinId;

    private String toBinCode;

    /*
     * Remarks
     */
    private String remarks;

    /*
     * Audit
     */
    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}