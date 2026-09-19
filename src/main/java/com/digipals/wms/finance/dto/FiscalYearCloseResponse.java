package com.digipals.wms.finance.dto;

import java.math.BigDecimal;

public record FiscalYearCloseResponse(
    int fiscalYear,
    int nextFiscalYear,
    String closingDocumentNumber,
    String openingDocumentNumber,
    BigDecimal netIncomeTransferred,
    int closingLines,
    int openingLines
) {}