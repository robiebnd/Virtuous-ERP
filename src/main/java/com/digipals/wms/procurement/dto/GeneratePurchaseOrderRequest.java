package com.digipals.wms.procurement.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

import java.util.UUID;

@Data
public class GeneratePurchaseOrderRequest {

    /**
     * Existing internal identifier. Kept for integrations and backwards compatibility.
     */
    private UUID purchaseRequisitionId;

    /**
     * Preferred identifier for frontend/user-facing workflows.
     */
    private String purchaseRequisitionNumber;

    /**
     * Optional supplier override. When omitted, the supplier assigned to the
     * approved purchase requisition is used.
     */
    private UUID supplierId;

    @AssertTrue(message = "Either purchaseRequisitionNumber or purchaseRequisitionId is required")
    public boolean hasPurchaseRequisitionReference() {
        return (purchaseRequisitionNumber != null && !purchaseRequisitionNumber.isBlank())
                || purchaseRequisitionId != null;
    }
}
