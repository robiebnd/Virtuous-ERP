package com.digipals.wms.stockcount.dto;

import com.digipals.wms.stockcount.entity.StockCountStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StockCountResponse {

    private UUID id;

    private String countNumber;

    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private StockCountStatus status;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    private Integer totalLines;

    private Integer countedLines;

    private Integer varianceLines;

    private BigDecimal totalVariance;

}
