package com.digipals.wms.finance.service;

import com.digipals.wms.finance.dto.FinanceControlSummary;
import com.digipals.wms.finance.dto.InventoryGlReconciliationResponse;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanceControlServiceTest {

    @Mock FiscalPeriodRepository fiscalPeriods;
    @Mock AccountingDocumentRepository documents;
    @Mock BankTransactionRepository bankTransactions;
    @Mock InventoryGlReconciliationService inventoryReconciliationService;
    @Mock TaxFilingRecordRepository taxFilings;

    private FinanceControlService service;

    @BeforeEach
    void setUp() {
        service = new FinanceControlService(
                fiscalPeriods, documents, bankTransactions,
                inventoryReconciliationService, taxFilings
        );
    }

    @Test
    void buildsFinanceControlSummary() {
        LocalDate today = LocalDate.now();
        FiscalPeriod current = FiscalPeriod.builder()
                .companyCode("ZW01")
                .fiscalYear(today.getYear())
                .periodNumber(today.getMonthValue())
                .periodName("Current")
                .startDate(today.withDayOfMonth(1))
                .endDate(today.withDayOfMonth(today.lengthOfMonth()))
                .status("OPEN")
                .build();

        AccountingDocument duplicateOne = AccountingDocument.builder()
                .companyCode("ZW01").status("POSTED")
                .referenceType("VENDOR_INVOICE").referenceNumber("INV-001").build();
        AccountingDocument duplicateTwo = AccountingDocument.builder()
                .companyCode("ZW01").status("POSTED")
                .referenceType("vendor_invoice").referenceNumber(" INV-001 ").build();
        AccountingDocument draft = AccountingDocument.builder()
                .companyCode("ZW01").status("DRAFT").build();
        AccountingDocument otherCompany = AccountingDocument.builder()
                .companyCode("OTHER").status("DRAFT").build();

        TaxFilingRecord ready = TaxFilingRecord.builder().companyCode("ZW01").status("READY").build();
        TaxFilingRecord filed = TaxFilingRecord.builder().companyCode("ZW01").status("FILED").build();

        when(fiscalPeriods.findByCompanyCodeAndFiscalYearOrderByPeriodNumber("ZW01", today.getYear()))
                .thenReturn(List.of(current));
        when(bankTransactions.findByStatusOrderByTransactionDateDesc("UNRECONCILED"))
                .thenReturn(List.of(mock(com.digipals.wms.finance.entity.BankTransaction.class)));
        when(documents.findAll()).thenReturn(List.of(duplicateOne, duplicateTwo, draft, otherCompany));
        when(inventoryReconciliationService.reconcile()).thenReturn(
                new InventoryGlReconciliationResponse(
                        new BigDecimal("10.00"),
                        new BigDecimal("10.00"),
                        BigDecimal.ZERO,
                        true
                )
        );
        when(taxFilings.findByCompanyCodeOrderByPeriodEndDesc("ZW01"))
                .thenReturn(List.of(ready, filed));

        FinanceControlSummary result = service.summary("zw01");

        assertEquals("ZW01", result.companyCode());
        assertEquals(today.getYear(), result.fiscalYear());
        assertEquals(today.getMonthValue(), result.currentPeriod());
        assertEquals("OPEN", result.currentPeriodStatus());
        assertEquals(1, result.openFiscalPeriods());
        assertEquals(1, result.unreconciledBankTransactions());
        assertEquals(1, result.duplicatePostingReferences());
        assertEquals(1, result.nonPostedAccountingDocuments());
        assertEquals(BigDecimal.ZERO, result.inventoryGlVariance());
        assertTrue(result.inventoryReconciled());
        assertEquals(1, result.taxFilingsReady());
        assertEquals(1, result.taxFilingsFiled());
    }
}
