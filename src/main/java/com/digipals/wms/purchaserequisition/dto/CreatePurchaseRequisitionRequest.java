package com.digipals.wms.purchaserequisition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePurchaseRequisitionRequest {

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    @NotBlank(message = "Department is required")
    private String department;

    private String remarks;
}
