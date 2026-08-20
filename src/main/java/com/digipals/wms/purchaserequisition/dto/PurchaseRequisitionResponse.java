package com.digipals.wms.purchaserequisition.dto;

import com.digipals.wms.purchaserequisition.entity.PurchaseRequisitionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private String currency;
    private String remarks;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String rejectionReason;

    private UUID requestedById;
    private UUID approvedById;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID rejectedById;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UUID cancelledById;

    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime rejectedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;
    private List<LineResponse> lines;

    @Builder
    @Data
    public static class LineResponse {
        private UUID id;
        private UUID productId;
        private String sku;
        private String productName;
        private BigDecimal quantity;
        private BigDecimal estimatedUnitCost;
        private BigDecimal estimatedLineTotal;
        private UUID sourceSupplierId;
        private String sourceSupplierCode;
        private String sourceSupplierName;
        private UUID purchasingInfoRecordId;
        private Integer plannedDeliveryDays;
        private String supplierItemCode;
        private String remarks;
    }
}
