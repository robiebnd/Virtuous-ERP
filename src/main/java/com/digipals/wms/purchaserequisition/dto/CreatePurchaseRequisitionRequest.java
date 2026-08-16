package com.digipals.wms.purchaserequisition.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.UUID;

@Data
public class CreatePurchaseRequisitionRequest {

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    @NotNull(message = "Supplier is required")
    private UUID supplierId;

    @NotBlank(message = "Department is required")
    private String department;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Za-z]{3}$", message = "Currency must be a 3-letter ISO currency code, e.g. USD")
    private String currency;

    private String remarks;
}
