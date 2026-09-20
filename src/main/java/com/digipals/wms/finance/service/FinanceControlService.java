package com.digipals.wms.finance.service;

import com.digipals.wms.finance.dto.FinanceControlSummary;
import com.digipals.wms.finance.entity.AccountingDocument;
import com.digipals.wms.finance.entity.FiscalPeriod;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceControlService {
    private final FiscalPeriodRepository fiscalPeriods;
    private final AccountingDocumentRepository documents;
    private final BankTransactionRepository bankTransactions;
    private final InventoryGlReconciliationService inventoryReconciliationService;
    private final TaxFilingRecordRepository taxFilings;

    public FinanceControlSummary summary(String companyCode) {
        String company = companyCode == null || companyCode.isBlank() ? "ZW01" : companyCode.trim().toUpperCase(Locale.ROOT);
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int period = today.getMonthValue();

        List<FiscalPeriod> yearPeriods = fiscalPeriods.findByCompanyCodeAndFiscalYearOrderByPeriodNumber(company, year);
        FiscalPeriod current = yearPeriods.stream()
                .filter(p -> !today.isBefore(p.getStartDate()) && !today.isAfter(p.getEndDate()))
                .findFirst().orElse(null);

        long openPeriods = yearPeriods.stream().filter(p -> "OPEN".equals(p.getStatus())).count();
        long unreconciled = bankTransactions.findByStatusOrderByTransactionDateDesc("UNRECONCILED").size();

        List<AccountingDocument> allDocuments = documents.findAll();
        long nonPosted = allDocuments.stream()
                .filter(d -> company.equalsIgnoreCase(d.getCompanyCode()))
                .filter(d -> !"POSTED".equals(d.getStatus()))
                .count();

        Map<String, Long> referenceCounts = allDocuments.stream()
                .filter(d -> company.equalsIgnoreCase(d.getCompanyCode()))
                .filter(d -> "POSTED".equals(d.getStatus()))
                .filter(d -> d.getReferenceType() != null && d.getReferenceNumber() != null && !d.getReferenceNumber().isBlank())
                .collect(Collectors.groupingBy(d -> d.getReferenceType().trim().toUpperCase(Locale.ROOT) + "|" + d.getReferenceNumber().trim(), Collectors.counting()));

        long duplicateReferences = referenceCounts.values().stream().filter(count -> count > 1).mapToLong(count -> count - 1).sum();

        var reconciliation = inventoryReconciliationService.reconcile();
        List<com.digipals.wms.finance.entity.TaxFilingRecord> filings = taxFilings.findByCompanyCodeOrderByPeriodEndDesc(company);
        long ready = filings.stream().filter(f -> "READY".equalsIgnoreCase(f.getStatus())).count();
        long filed = filings.stream().filter(f -> "FILED".equalsIgnoreCase(f.getStatus())).count();

        return new FinanceControlSummary(
                company,
                year,
                period,
                current == null ? "NOT_CONFIGURED" : current.getStatus(),
                openPeriods,
                unreconciled,
                duplicateReferences,
                nonPosted,
                reconciliation.variance(),
                reconciliation.balanced(),
                ready,
                filed
        );
    }
}