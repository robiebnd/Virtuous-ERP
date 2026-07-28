package com.digipals.wms.purchaseorders.dto;

import com.digipals.wms.purchaseorders.entity.ProcurementSource;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePurchaseOrderRequest {

    @NotNull
    private UUID supplierId;

    @NotNull
    private UUID warehouseId;

    private UUID purchaseRequisitionId;

    private ProcurementSource source;
}