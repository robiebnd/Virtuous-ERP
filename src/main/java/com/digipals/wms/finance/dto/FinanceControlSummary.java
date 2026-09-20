package com.digipals.wms.finance.dto;

import java.math.BigDecimal;

public record FinanceControlSummary(
        String companyCode,
        int fiscalYear,
        int currentPeriod,
        String currentPeriodStatus,
        long openFiscalPeriods,
        long unreconciledBankTransactions,
        long duplicatePostingReferences,
        long nonPostedAccountingDocuments,
        BigDecimal inventoryGlVariance,
        boolean inventoryReconciled,
        long taxFilingsReady,
        long taxFilingsFiled
) {}