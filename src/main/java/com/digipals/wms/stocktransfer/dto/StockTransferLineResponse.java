package com.digipals.wms.stocktransfer.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransferLineResponse {

    private UUID id;

    private UUID transferId;

    private UUID productId;

    private String sku;

    private String productName;

    private BigDecimal quantity;
}