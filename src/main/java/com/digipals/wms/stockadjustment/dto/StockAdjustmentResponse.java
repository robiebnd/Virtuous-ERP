package com.digipals.wms.stockadjustment.dto;

import com.digipals.wms.stockadjustment.entity.AdjustmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAdjustmentResponse {

    private UUID id;

    private String adjustmentNumber;

    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private AdjustmentStatus status;

    private String reason;

    private String remarks;

    private LocalDateTime createdAt;

    private LocalDateTime postedAt;
}