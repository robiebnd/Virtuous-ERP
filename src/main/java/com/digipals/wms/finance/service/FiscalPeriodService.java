package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.FiscalPeriodRequest;
import com.digipals.wms.finance.entity.FiscalPeriod;
import com.digipals.wms.finance.repository.FiscalPeriodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class FiscalPeriodService {
    public static final String COMPANY_CODE = "ZW01";
    public static final String OPEN = "OPEN";
    public static final String CLOSED = "CLOSED";
    public static final String PERMANENTLY_CLOSED = "PERMANENTLY_CLOSED";

    private final FiscalPeriodRepository repository;

    @Transactional(readOnly = true)
    public List<FiscalPeriod> list(String companyCode, int year) {
        return repository.findByCompanyCodeAndFiscalYearOrderByPeriodNumber(companyCode, year);
    }

    public List<FiscalPeriod> createYear(FiscalPeriodRequest request) {
        String company = normalizeCompany(request.companyCode());
        if (!repository.findByCompanyCodeAndFiscalYearOrderByPeriodNumber(company, request.fiscalYear()).isEmpty()) {
            throw new InvalidWorkflowException("Fiscal year " + request.fiscalYear() + " already exists for " + company + ".");
        }
        for (int month = 1; month <= 12; month++) {
            LocalDate start = LocalDate.of(request.fiscalYear(), month, 1);
            LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
            repository.save(FiscalPeriod.builder()
                .companyCode(company).fiscalYear(request.fiscalYear()).periodNumber(month)
                .periodName(start.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + request.fiscalYear())
                .startDate(start).endDate(end).status(OPEN).build());
        }
        return list(company, request.fiscalYear());
    }

    public FiscalPeriod closePeriod(String companyCode, int year, int period, String closedBy, boolean permanent) {
        FiscalPeriod p = find(companyCode, year, period);
        if (!OPEN.equals(p.getStatus())) throw new InvalidWorkflowException("Only an open fiscal period can be closed.");
        if (period > 1) {
            FiscalPeriod prior = find(companyCode, year, period - 1);
            if (OPEN.equals(prior.getStatus())) {
                throw new InvalidWorkflowException("Close fiscal periods sequentially. " + prior.getPeriodName() + " is still open.");
            }
        }
        p.setStatus(permanent ? PERMANENTLY_CLOSED : CLOSED);
        p.setClosedAt(LocalDateTime.now());
        p.setClosedBy(closedBy == null || closedBy.isBlank() ? "SYSTEM" : closedBy.trim());
        return repository.save(p);
    }

    public FiscalPeriod reopenPeriod(String companyCode, int year, int period) {
        FiscalPeriod p = find(companyCode, year, period);
        if (!CLOSED.equals(p.getStatus())) throw new InvalidWorkflowException("Only a closed, non-permanent period can be reopened.");
        p.setStatus(OPEN);
        p.setClosedAt(null);
        p.setClosedBy(null);
        return repository.save(p);
    }

    @Transactional(readOnly = true)
    public FiscalPeriod find(String companyCode, int year, int period) {
        return repository.findByCompanyCodeAndFiscalYearAndPeriodNumber(normalizeCompany(companyCode), year, period)
            .orElseThrow(() -> new InvalidWorkflowException("Fiscal period not configured: " + companyCode + "/" + year + "/" + period));
    }

    @Transactional(readOnly = true)
    public void ensurePostingAllowed(LocalDateTime postingDate) {
        ensurePostingAllowed(COMPANY_CODE, postingDate);
    }

    @Transactional(readOnly = true)
    public void ensurePostingAllowed(String companyCode, LocalDateTime postingDate) {
        LocalDate date = postingDate.toLocalDate();
        String normalizedCompany = normalizeCompany(companyCode);
        FiscalPeriod p = repository.findByCompanyCodeAndStartDateLessThanEqualAndEndDateGreaterThanEqual(normalizedCompany, date, date)
            .orElseThrow(() -> new InvalidWorkflowException("No fiscal period is configured for posting date " + date + "."));
        if (!OPEN.equals(p.getStatus())) {
            throw new InvalidWorkflowException("Posting is blocked because fiscal period " + p.getPeriodName() + " is " + p.getStatus() + ".");
        }
    }

    private String normalizeCompany(String value) {
        String company = value == null ? COMPANY_CODE : value.trim().toUpperCase(Locale.ROOT);
        if (company.isBlank()) throw new InvalidWorkflowException("Company code is required.");
        return company;
    }
}
