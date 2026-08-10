package com.digipals.wms.purchaserequisition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdatePurchaseRequisitionRequest {

    @NotNull(message = "Supplier is required")
    private UUID supplierId;

    @NotBlank(message = "Department is required")
    private String department;

    private String remarks;
}
