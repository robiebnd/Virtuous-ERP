package com.digipals.wms.stockcount.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateStockCountRequest {

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    private String remarks;

    private LocalDateTime countDate;
}
