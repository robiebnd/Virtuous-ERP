package com.digipals.wms.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ApplyCashRequest(
        @NotNull UUID billingDocumentId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount
) {
}
