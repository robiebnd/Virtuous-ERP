package com.digipals.wms.inventorybin.dto;

import com.digipals.wms.bin.entity.BinType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryBinResponse {

    private UUID id;

    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private UUID binId;

    private String binCode;

    private String binName;

    private BinType binType;

    private UUID productId;

    private String sku;

    private String productName;

    private BigDecimal quantityOnHand;

    private BigDecimal quantityReserved;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}