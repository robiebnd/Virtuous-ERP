package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.entity.FiscalPeriod;
import com.digipals.wms.finance.repository.FiscalPeriodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FiscalPeriodServiceTest {

    @Mock FiscalPeriodRepository repository;

    private FiscalPeriodService service;

    @BeforeEach
    void setUp() {
        service = new FiscalPeriodService(repository);
    }

    @Test
    void blocksPostingWhenPeriodIsClosed() {
        FiscalPeriod period = FiscalPeriod.builder()
                .companyCode("ZW01").fiscalYear(2026).periodNumber(9)
                .periodName("Sep 2026").startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2026, 9, 30)).status(FiscalPeriodService.CLOSED).build();

        when(repository.findByCompanyCodeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                "ZW01", LocalDate.of(2026, 9, 21), LocalDate.of(2026, 9, 21)
        )).thenReturn(Optional.of(period));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.ensurePostingAllowed("ZW01", LocalDate.of(2026, 9, 21).atStartOfDay()));

        assertTrue(ex.getMessage().contains("CLOSED"));
    }

    @Test
    void blocksClosingPeriodOutOfSequence() {
        FiscalPeriod current = FiscalPeriod.builder()
                .companyCode("ZW01").fiscalYear(2026).periodNumber(2)
                .periodName("Feb 2026").status(FiscalPeriodService.OPEN).build();
        FiscalPeriod prior = FiscalPeriod.builder()
                .companyCode("ZW01").fiscalYear(2026).periodNumber(1)
                .periodName("Jan 2026").status(FiscalPeriodService.OPEN).build();

        when(repository.findByCompanyCodeAndFiscalYearAndPeriodNumber("ZW01", 2026, 2))
                .thenReturn(Optional.of(current));
        when(repository.findByCompanyCodeAndFiscalYearAndPeriodNumber("ZW01", 2026, 1))
                .thenReturn(Optional.of(prior));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.closePeriod("ZW01", 2026, 2, "FINANCE", false));

        assertTrue(ex.getMessage().contains("sequentially"));
        verify(repository, never()).save(any(FiscalPeriod.class));
    }

    @Test
    void permanentlyClosedPeriodCannotBeReopened() {
        FiscalPeriod period = FiscalPeriod.builder()
                .companyCode("ZW01").fiscalYear(2026).periodNumber(1)
                .periodName("Jan 2026").status(FiscalPeriodService.PERMANENTLY_CLOSED).build();

        when(repository.findByCompanyCodeAndFiscalYearAndPeriodNumber("ZW01", 2026, 1))
                .thenReturn(Optional.of(period));

        InvalidWorkflowException ex = assertThrows(InvalidWorkflowException.class, () ->
                service.reopenPeriod("ZW01", 2026, 1));

        assertTrue(ex.getMessage().contains("Only a closed"));
        verify(repository, never()).save(any(FiscalPeriod.class));
    }
}
