package com.digipals.wms.purchaserequisition.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder
public class PurchaseRequisitionLineResponse {
    private UUID id;
    private UUID productId;
    private String sku;
    private String productName;
    private BigDecimal quantity;
    private BigDecimal estimatedUnitCost;
    private String itemCategory;
    private String accountAssignmentCategory;
    private String unitOfMeasure;
    private LocalDateTime requestedDeliveryDate;
    private BigDecimal valuationPrice;
    private UUID sourceSupplierId;
    private String sourceSupplierCode;
    private String sourceSupplierName;
    private UUID purchasingInfoRecordId;
    private Integer plannedDeliveryDays;
    private String supplierItemCode;
    private String remarks;
}