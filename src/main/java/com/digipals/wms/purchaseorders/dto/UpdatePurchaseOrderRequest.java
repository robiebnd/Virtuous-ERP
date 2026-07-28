package com.digipals.wms.purchaseorders.dto;

import com.digipals.wms.purchaseorders.entity.ProcurementSource;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdatePurchaseOrderRequest {

    @NotNull(message = "Supplier is required")
    private UUID supplierId;

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    private UUID purchaseRequisitionId;

    @NotNull(message = "Procurement source is required")
    private ProcurementSource source;
}