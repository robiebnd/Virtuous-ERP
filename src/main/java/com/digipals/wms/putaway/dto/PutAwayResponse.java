package com.digipals.wms.putaway.dto;

import com.digipals.wms.putaway.entity.PutAwayStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PutAwayResponse {

    private UUID id;

    private String putAwayNumber;

    private UUID goodsReceiptId;

    private String grnNumber;

    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private PutAwayStatus status;

    private String remarks;

    private UUID initiatedById;

    private String initiatedBy;

    private UUID completedById;

    private String completedBy;

    private LocalDateTime completedAt;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}