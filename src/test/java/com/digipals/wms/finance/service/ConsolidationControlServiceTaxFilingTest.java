package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.*;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsolidationControlServiceTaxFilingTest {

    @Mock GroupReportingRunRepository runs;
    @Mock GroupReportingBalanceRepository balances;
    @Mock ConsolidationGroupRepository groups;
    @Mock ConsolidationJournalRepository journals;
    @Mock ConsolidationAuditEventRepository audits;
    @Mock CopaAllocationRuleRepository copaRules;
    @Mock CopaAllocationTargetRepository copaTargets;
    @Mock CopaAllocationRunRepository copaRuns;
    @Mock ProfitabilitySegmentRepository segments;
    @Mock ProfitabilityService profitability;
    @Mock CopaAllocationResultRepository copaResults;
    @Mock CompanyCodeRepository companies;
    @Mock TaxAccountingService taxAccounting;
    @Mock TaxFilingRecordRepository filings;

    private ConsolidationControlService service;
    private final LocalDate start = LocalDate.of(2026, 9, 1);
    private final LocalDate end = LocalDate.of(2026, 9, 30);

    @BeforeEach
    void setUp() {
        service = new ConsolidationControlService(
                runs, balances, groups, journals, audits, copaRules, copaTargets,
                copaRuns, segments, profitability, copaResults, companies, taxAccounting, filings
        );
    }

    private TaxReportSummary summary(BigDecimal output, BigDecimal input, BigDecimal recoverable, BigDecimal net) {
        return new TaxReportSummary("ZW01", start, end, output, input, recoverable, net, java.util.List.of());
    }

    private TaxFilingRequest request() {
        return new TaxFilingRequest("ZW01", "VAT", start, end, "VAT-2026-09", "September VAT", "FINANCE");
    }

    @Test
    void preparesTaxFilingWithCurrentTotals() {
        CompanyCode company = CompanyCode.builder().companyCode("ZW01").companyName("Virtuous").countryCode("ZWE").functionalCurrency("USD").reportingCurrency("USD").build();
        when(companies.findByCompanyCodeIgnoreCase("ZW01")).thenReturn(Optional.of(company));
        when(taxAccounting.report("ZW01", start, end)).thenReturn(summary(
                new BigDecimal("150.00"), new BigDecimal("50.00"), new BigDecimal("50.00"), new BigDecimal("100.00")
        ));
        when(filings.findByCompanyCodeAndTaxTypeAndPeriodStartAndPeriodEnd("ZW01", "VAT", start, end))
                .thenReturn(Optional.empty());
        when(filings.save(any(TaxFilingRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaxFilingRecord result = service.prepareFiling(request());

        assertEquals("READY", result.getStatus());
        assertEquals(new BigDecimal("150.00"), result.getOutputTax());
        assertEquals(new BigDecimal("50.00"), result.getInputTax());
        assertEquals(new BigDecimal("100.00"), result.getNetTax());
        verify(filings).save(any(TaxFilingRecord.class));
    }

    @Test
    void blocksFilingWhenTotalsChangedAfterPreparation() {
        TaxFilingRecord ready = TaxFilingRecord.builder()
                .companyCode("ZW01").taxType("VAT").periodStart(start).periodEnd(end)
                .status("READY").taxableBase(new BigDecimal("1000.00"))
                .outputTax(new BigDecimal("150.00")).inputTax(new BigDecimal("50.00"))
                .recoverableInputTax(new BigDecimal("50.00")).netTax(new BigDecimal("100.00"))
                .build();

        when(filings.findByCompanyCodeAndTaxTypeAndPeriodStartAndPeriodEnd("ZW01", "VAT", start, end))
                .thenReturn(Optional.of(ready));
        when(taxAccounting.report("ZW01", start, end)).thenReturn(summary(
                new BigDecimal("175.00"), new BigDecimal("50.00"), new BigDecimal("50.00"), new BigDecimal("125.00")
        ));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () -> service.file(request()));

        assertTrue(ex.getMessage().contains("totals changed"));
        assertEquals("READY", ready.getStatus());
        verify(filings, never()).save(any(TaxFilingRecord.class));
    }

    @Test
    void filesReadyTaxFilingAndLocksIt() {
        TaxFilingRecord ready = TaxFilingRecord.builder()
                .companyCode("ZW01").taxType("VAT").periodStart(start).periodEnd(end)
                .status("READY").taxableBase(new BigDecimal("1000.00"))
                .outputTax(new BigDecimal("150.00")).inputTax(new BigDecimal("50.00"))
                .recoverableInputTax(new BigDecimal("50.00")).netTax(new BigDecimal("100.00"))
                .build();

        when(filings.findByCompanyCodeAndTaxTypeAndPeriodStartAndPeriodEnd("ZW01", "VAT", start, end))
                .thenReturn(Optional.of(ready));
        when(taxAccounting.report("ZW01", start, end)).thenReturn(summary(
                new BigDecimal("150.00"), new BigDecimal("50.00"), new BigDecimal("50.00"), new BigDecimal("100.00")
        ));
        when(filings.save(any(TaxFilingRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaxFilingRecord result = service.file(request());

        assertEquals("FILED", result.getStatus());
        assertEquals("FINANCE", result.getFiledBy());
        assertNotNull(result.getFiledAt());
        verify(filings).save(ready);
    }
}
