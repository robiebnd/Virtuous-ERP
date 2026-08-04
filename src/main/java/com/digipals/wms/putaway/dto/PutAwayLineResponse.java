package com.digipals.wms.putaway.dto;

import com.digipals.wms.putaway.entity.PutAwayLineStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PutAwayLineResponse {

    private UUID id;

    private UUID putAwayId;

    private UUID goodsReceiptLineId;

    private UUID productId;

    private String sku;

    private String productName;

    private UUID fromBinId;

    private String fromBinCode;

    private String fromBinName;

    private UUID toBinId;

    private String toBinCode;

    private String toBinName;

    private BigDecimal plannedQuantity;

    private BigDecimal completedQuantity;

    private BigDecimal remainingQuantity;

    private PutAwayLineStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}