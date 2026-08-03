package com.digipals.wms.inventorybin.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInventoryBinRequest {

    private BigDecimal quantityOnHand;

    private BigDecimal quantityReserved;
}