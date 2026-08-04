package com.digipals.wms.putaway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PutAwayLineResponse {

    private UUID id;

    private UUID goodsReceiptLineId;

    private UUID productId;

    private String sku;

    private String productName;

    private UUID fromBinId;

    private String fromBinCode;

    private UUID toBinId;

    private String toBinCode;

    private BigDecimal plannedQuantity;

    private BigDecimal completedQuantity;
}