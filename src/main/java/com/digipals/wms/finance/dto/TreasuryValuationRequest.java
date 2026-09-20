package com.digipals.wms.finance.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TreasuryValuationRequest(
        @NotBlank String instrumentNumber,
        @NotNull LocalDate valuationDate,
        @NotNull BigDecimal valuationAmount,
        @NotBlank String balanceAccountCode,
        @NotBlank String gainAccountCode,
        @NotBlank String lossAccountCode,
        @Pattern(regexp="[A-Za-z]{3}") String currency
) {}