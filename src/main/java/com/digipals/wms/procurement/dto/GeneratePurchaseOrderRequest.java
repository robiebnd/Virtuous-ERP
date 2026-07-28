package com.digipals.wms.procurement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GeneratePurchaseOrderRequest {

    @NotNull
    private UUID purchaseRequisitionId;

    @NotNull
    private UUID supplierId;
}