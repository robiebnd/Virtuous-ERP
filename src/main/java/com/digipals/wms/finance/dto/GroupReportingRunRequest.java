package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.*; public record GroupReportingRunRequest(@NotNull Integer fiscalYear,@NotNull @Min(1) @Max(12) Integer periodNumber){}