package com.digipals.wms.purchaserequisition.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SetPurchaseRequisitionLineSourceRequest {

    @NotNull(message = "Purchasing Info Record is required")
    private UUID purchasingInfoRecordId;
}
