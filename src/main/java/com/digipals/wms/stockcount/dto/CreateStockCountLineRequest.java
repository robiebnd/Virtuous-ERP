package com.digipals.wms.stockcount.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateStockCountLineRequest {

    private UUID stockCountId;

    private UUID productId;

    private BigDecimal countedQuantity;

    private String reason;

}
