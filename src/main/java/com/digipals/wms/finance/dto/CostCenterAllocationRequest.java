package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CostCenterAllocationRequest(@NotBlank String sourceCostCenter, @NotBlank String targetCostCenter, @NotBlank String expenseAccountCode, @NotNull @DecimalMin("0.01") BigDecimal amount, @NotBlank @Size(max=100) String referenceNumber, @Size(max=500) String description) {}