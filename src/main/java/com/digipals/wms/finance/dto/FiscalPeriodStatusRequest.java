package com.digipals.wms.finance.dto;

import jakarta.validation.constraints.NotBlank;

public record FiscalPeriodStatusRequest(@NotBlank String status, String closedBy) {}