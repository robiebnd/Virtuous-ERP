package com.digipals.wms.salesorder.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateSalesOrderItemRequest(
        @NotBlank String materialCode,
        @NotNull @DecimalMin("0.001") BigDecimal quantity,
        @DecimalMin("0.00") BigDecimal unitPrice
) {
}
