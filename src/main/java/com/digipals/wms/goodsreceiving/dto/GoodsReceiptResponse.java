package com.digipals.wms.goodsreceiving.dto;

import com.digipals.wms.goodsreceiving.entity.ReceiptStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class GoodsReceiptResponse {

    private UUID id;

    private String grnNumber;

    private ReceiptStatus status;

    private UUID purchaseOrderId;

    private String purchaseOrderNumber;

    private UUID warehouseId;

    private String warehouseCode;

    private String warehouseName;

    private UUID receivedById;

    private String receivedBy;

    private UUID approvedById;

    private String approvedBy;

    private String supplierDeliveryNote;

    private String remarks;

    private Boolean active;

    private LocalDateTime receivedDate;

    private LocalDateTime approvedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}