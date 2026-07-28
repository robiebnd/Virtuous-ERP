package com.digipals.wms.stockcount.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateStockCountRequest {

    private UUID warehouseId;

    private String remarks;

    private LocalDateTime countDate;

}