package com.digipals.wms.finance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FiscalPeriodRequest(
    @NotBlank String companyCode,
    @NotNull @Min(2000) Integer fiscalYear
) {}