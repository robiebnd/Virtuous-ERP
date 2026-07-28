package com.digipals.wms.stockcount.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class StockCountLineResponse {

    private UUID id;

    private UUID stockCountId;

    private UUID productId;
   
    private String sku;

    private String productName;

    private BigDecimal systemQuantity;

    private BigDecimal countedQuantity;

    private BigDecimal variance;

    private String reason;

}
