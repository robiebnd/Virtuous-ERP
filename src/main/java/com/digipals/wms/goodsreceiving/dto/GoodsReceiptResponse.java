package com.digipals.wms.goodsreceiving.dto;

import com.digipals.wms.goodsreceiving.entity.ReceiptStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodsReceiptResponse {

    private UUID id;
    private String grnNumber;
    private ReceiptStatus status;

    private UUID purchaseOrderId;
    private String purchaseOrderNumber;
    private String currency;

    private UUID supplierId;
    private String supplierCode;
    private String supplierName;

    private UUID warehouseId;
    private String warehouseCode;
    private String warehouseName;

    private UUID receivedById;
    private String receivedBy;

    private UUID approvedById;
    private String approvedBy;
    private LocalDateTime approvedAt;

    private String supplierDeliveryNote;
    private String remarks;

    private List<GoodsReceiptLineResponse> lines;

    private Boolean active;
    private LocalDateTime receivedDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
