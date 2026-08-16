package com.digipals.wms.purchaseorders.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.digipals.wms.purchaseorders.entity.ProcurementSource;
import com.digipals.wms.purchaseorders.entity.PurchaseOrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PurchaseOrderResponse {

    private UUID id;
    private String poNumber;
    private PurchaseOrderStatus status;
    private ProcurementSource source;
    private String currency;
    private UUID supplierId;
    private String supplierCode;
    private String supplierName;
    private UUID warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private UUID purchaseRequisitionId;
    private String purchaseRequisitionNumber;
    private UUID createdById;
    private String createdBy;
    private UUID approvedById;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<PurchaseOrderLineResponse> lines;
}
