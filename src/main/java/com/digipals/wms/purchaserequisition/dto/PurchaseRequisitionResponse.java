package com.digipals.wms.purchaserequisition.dto;

import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
public class PurchaseRequisitionResponse {

    private UUID id;

    private String requisitionNumber;

    private String warehouseCode;

    private String warehouseName;

    private UUID supplierId;

    private String supplierCode;

    private String supplierName;

    private PurchaseRequisitionStatus status;

    private String department;

    private String remarks;

    private String rejectionReason;

    private UUID requestedById;

    private UUID approvedById;

    private UUID rejectedById;

    private UUID cancelledById;

    private LocalDateTime submittedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
}
