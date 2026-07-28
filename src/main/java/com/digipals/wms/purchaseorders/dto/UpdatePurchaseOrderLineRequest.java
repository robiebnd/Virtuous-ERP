package com.digipals.wms.purchaseorders.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdatePurchaseOrderLineRequest {

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.00")
    private BigDecimal discount;

    @DecimalMin(value = "0.00")
    private BigDecimal tax;

    private String remarks;
}