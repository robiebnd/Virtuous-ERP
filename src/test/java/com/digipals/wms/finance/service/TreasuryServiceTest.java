package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.TreasuryInstrumentRequest;
import com.digipals.wms.finance.dto.TreasuryValuationRequest;
import com.digipals.wms.finance.entity.TreasuryInstrument;
import com.digipals.wms.finance.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TreasuryServiceTest {

    @Mock BankAccountRepository bankAccounts;
    @Mock BankTransactionRepository transactions;
    @Mock LiquidityForecastRepository forecasts;
    @Mock AccountingDocumentRepository documents;
    @Mock FinanceQueryService financeQueryService;
    @Mock TreasuryInstrumentRepository instruments;
    @Mock TreasuryRiskLimitRepository riskLimits;
    @Mock FinancePostingService postingService;

    private TreasuryService service;

    @BeforeEach
    void setUp() {
        service = new TreasuryService(
                bankAccounts, transactions, forecasts, documents,
                financeQueryService, instruments, riskLimits, postingService
        );
    }

    @Test
    void rejectsDuplicateInstrumentNumber() {
        when(instruments.findByInstrumentNumber("TR-001"))
                .thenReturn(Optional.of(TreasuryInstrument.builder()
                        .instrumentNumber("TR-001")
                        .instrumentType("FORWARD")
                        .counterparty("BANK")
                        .currency("USD")
                        .notionalAmount(new BigDecimal("1000.00"))
                        .tradeDate(LocalDate.of(2026, 9, 1))
                        .status("OPEN")
                        .build()));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.createInstrument(new TreasuryInstrumentRequest(
                        "TR-001", "FORWARD", "BANK", "USD",
                        new BigDecimal("500.00"),
                        LocalDate.of(2026, 9, 21),
                        LocalDate.of(2026, 12, 31),
                        false
                ));

        assertEquals("Treasury instrument already exists.", ex.getMessage());
        verify(instruments, never()).save(any());
    }

    @Test
    void rejectsExposureAboveActiveRiskLimit() {
        when(instruments.findByInstrumentNumber("TR-002")).thenReturn(Optional.empty());
        when(instruments.findByStatusOrderByMaturityDateAsc("OPEN")).thenReturn(List.of());
        var limit = com.digipals.wms.finance.entity.TreasuryRiskLimit.builder()
                .limitCode("CP-BANK-USD")
                .counterparty("BANK")
                .currency("USD")
                .instrumentType("FORWARD")
                .limitAmount(new BigDecimal("1000.00"))
                .active(true)
                .build();
        when(riskLimits.findAll()).thenReturn(List.of(limit));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.createInstrument(new TreasuryInstrumentRequest(
                        "TR-002", "FORWARD", "BANK", "USD",
                        new BigDecimal("1000.01"),
                        LocalDate.of(2026, 9, 21),
                        LocalDate.of(2026, 12, 31),
                        false
                ));

        assertTrue(ex.getMessage().contains("risk limit exceeded"));
        verify(instruments, never()).save(any());
    }

    @Test
    void rejectsValuationBeforePreviousValuationDate() {
        UUID id = UUID.randomUUID();
        TreasuryInstrument instrument = TreasuryInstrument.builder()
                .instrumentNumber("TR-003")
                .instrumentType("FORWARD")
                .counterparty("BANK")
                .currency("USD")
                .notionalAmount(new BigDecimal("5000.00"))
                .tradeDate(LocalDate.of(2026, 9, 1))
                .maturityDate(LocalDate.of(2026, 12, 31))
                .status("OPEN")
                .valuationAmount(new BigDecimal("100.00"))
                .lastValuationDate(LocalDate.of(2026, 9, 20))
                .build();
        instrument.setId(id);
        when(instruments.findByInstrumentNumber("TR-003")).thenReturn(Optional.of(instrument));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.valueInstrument(new TreasuryValuationRequest(
                        "TR-003",
                        LocalDate.of(2026, 9, 20),
                        new BigDecimal("125.00"),
                        "120000",
                        "490000",
                        "590000",
                        "USD"
                ));

        assertTrue(ex.getMessage().contains("chronological order"));
        verifyNoInteractions(postingService);
        verify(instruments, never()).save(any());
    }

    @Test
    void rejectsZeroValuationMovement() {
        TreasuryInstrument instrument = TreasuryInstrument.builder()
                .instrumentNumber("TR-004")
                .instrumentType("FORWARD")
                .counterparty("BANK")
                .currency("USD")
                .notionalAmount(new BigDecimal("5000.00"))
                .tradeDate(LocalDate.of(2026, 9, 1))
                .maturityDate(LocalDate.of(2026, 12, 31))
                .status("OPEN")
                .valuationAmount(new BigDecimal("100.00"))
                .build();
        instrument.setId(UUID.randomUUID());
        when(instruments.findByInstrumentNumber("TR-004")).thenReturn(Optional.of(instrument));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.valueInstrument(new TreasuryValuationRequest(
                        "TR-004",
                        LocalDate.of(2026, 9, 21),
                        new BigDecimal("100.00"),
                        "120000",
                        "490000",
                        "590000",
                        "USD"
                ));

        assertTrue(ex.getMessage().contains("difference is zero"));
        verifyNoInteractions(postingService);
        verify(instruments, never()).save(any());
    }
}
