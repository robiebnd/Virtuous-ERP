package com.digipals.wms.stocktransfer.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class CreateStockTransferRequest {

    private UUID sourceWarehouseId;

    private UUID destinationWarehouseId;

    private String remarks;
}