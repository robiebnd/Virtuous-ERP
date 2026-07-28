package com.digipals.wms.stocktransfer.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateStockTransferLineRequest {

    private UUID stockTransferId;

    private UUID productId;

    private BigDecimal quantity;
}