package com.digipals.wms.putaway.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreatePutAwayLineRequest {

    @NotNull(message = "Put Away is required")
    private UUID putAwayId;

    @NotNull(message = "Goods Receipt Line is required")
    private UUID goodsReceiptLineId;

    @NotNull(message = "Staging bin is required")
    private UUID fromBinId;

    @NotNull(message = "Planned quantity is required")
    @DecimalMin(value = "0.01")
    private BigDecimal plannedQuantity;
}