package com.digipals.wms.inventorybin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryBinResponse {

    private UUID id;

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

    private String binName;

    /*
     * Product
     */
    private UUID productId;

    private String sku;

    private String productName;

    /*
     * Stock
     */
    private BigDecimal quantityOnHand;

    private BigDecimal quantityReserved;

    private BigDecimal quantityAvailable;

    /*
     * Audit
     */
    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}