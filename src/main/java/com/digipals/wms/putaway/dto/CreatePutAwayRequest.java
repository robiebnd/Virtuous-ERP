package com.digipals.wms.putaway.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePutAwayRequest {

    @NotNull(message = "Goods Receipt is required")
    private UUID goodsReceiptId;

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    @NotNull(message = "Staging bin is required")
    private UUID fromBinId;

    private String remarks;
}