package com.digipals.wms.purchaserequisition.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePurchaseRequisitionRequest {

    @NotBlank(message = "Department is required")
    private String department;

    private String remarks;
}
