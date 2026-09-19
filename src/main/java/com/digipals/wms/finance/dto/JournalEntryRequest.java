package com.digipals.wms.finance.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;

public record JournalEntryRequest(
        @NotBlank String documentType,
        String referenceNumber,
        @NotBlank String currency,
        String companyCode,
        LocalDate postingDate,
        @NotBlank String description,
        @NotEmpty List<@Valid Line> lines
) {
    public record Line(
            @NotBlank String accountCode,
            @DecimalMin(value = "0.00") BigDecimal debit,
            @DecimalMin(value = "0.00") BigDecimal credit,
            String costCenter,
            String profitCenter,
            String functionalArea,
            String segment,
            String lineText,
            String internalOrderCode,
            String wbsElement
    ) {}
}
