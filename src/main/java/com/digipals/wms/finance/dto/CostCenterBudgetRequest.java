package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record CostCenterBudgetRequest(@NotBlank String costCenterCode, @NotNull @Min(2000) Integer fiscalYear, @NotNull @DecimalMin("0.00") BigDecimal budgetAmount, @Pattern(regexp="[A-Za-z]{3}") String currency, @Size(max=250) String description) {}