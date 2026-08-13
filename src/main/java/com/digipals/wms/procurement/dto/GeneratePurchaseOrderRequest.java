package com.digipals.wms.procurement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class GeneratePurchaseOrderRequest {

    @NotNull
    private UUID purchaseRequisitionId;

    /**
     * Optional supplier override. When omitted, the supplier assigned to the
     * approved purchase requisition is used.
     */
    private UUID supplierId;
}
