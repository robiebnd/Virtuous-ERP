package com.digipals.wms.inventorybin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateInventoryBinRequest {

    @NotNull
    private UUID warehouseId;

    @NotNull
    private UUID binId;

    @NotNull
    private UUID productId;

    private BigDecimal quantityOnHand;

    private BigDecimal quantityReserved;
}