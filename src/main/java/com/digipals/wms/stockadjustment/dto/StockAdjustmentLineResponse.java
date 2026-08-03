package com.digipals.wms.stockadjustment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentLineResponse {

    private UUID id;

    private UUID adjustmentId;

    private UUID binId;

    private String binCode;

    private UUID productId;

    private String sku;

    private String productName;

    private BigDecimal systemQuantity;

    private BigDecimal countedQuantity;

    private BigDecimal difference;

    private BigDecimal adjustmentQuantity;
}