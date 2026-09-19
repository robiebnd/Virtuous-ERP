package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.FiscalYearCloseResponse;
import com.digipals.wms.finance.entity.FiscalPeriod;
import com.digipals.wms.finance.repository.AccountingDocumentRepository;
import com.digipals.wms.finance.repository.AccountingLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FiscalYearCloseService {
    private static final String COMPANY_CODE = "ZW01";
    private static final String RETAINED_EARNINGS = "3200";
    private static final String CURRENT_YEAR_EARNINGS = "3300";
    private static final String POSTED = "POSTED";
    private static final String REF_TYPE = "FISCAL_YEAR_CLOSE";

    private final FiscalPeriodService fiscalPeriodService;
    private final AccountingLineRepository accountingLineRepository;
    private final AccountingDocumentRepository accountingDocumentRepository;
    private final FinancePostingService postingService;

    public FiscalYearCloseResponse closeAndCarryForward(int fiscalYear) {
        List<FiscalPeriod> periods = fiscalPeriodService.list(COMPANY_CODE, fiscalYear);
        if (periods.size() != 12) throw new InvalidWorkflowException("Fiscal year " + fiscalYear + " must have all 12 fiscal periods configured.");
        if (periods.stream().anyMatch(p -> FiscalPeriodService.OPEN.equals(p.getStatus()))) {
            throw new InvalidWorkflowException("All fiscal periods for " + fiscalYear + " must be closed before year-end carryforward.");
        }

        String closeRef = "FY" + fiscalYear + "-CLOSE";
        String openRef = "FY" + (fiscalYear + 1) + "-OPEN";
        if (accountingDocumentRepository.findFirstByReferenceTypeAndReferenceNumberAndStatus(REF_TYPE, closeRef, POSTED).isPresent()
            || accountingDocumentRepository.findFirstByReferenceTypeAndReferenceNumberAndStatus(REF_TYPE, openRef, POSTED).isPresent()) {
            throw new InvalidWorkflowException("Fiscal year " + fiscalYear + " has already been carried forward.");
        }

        if (fiscalPeriodService.list(COMPANY_CODE, fiscalYear + 1).isEmpty()) {
            fiscalPeriodService.createYear(new com.digipals.wms.finance.dto.FiscalPeriodRequest(COMPANY_CODE, fiscalYear + 1));
        }

        List<Object[]> balances = accountingLineRepository.yearEndBalances(fiscalYear);
        List<FinancePostingService.PostingLine> closing = new ArrayList<>();
        BigDecimal netIncome = BigDecimal.ZERO;
        BigDecimal currentYearEarnings = BigDecimal.ZERO;

        for (Object[] row : balances) {
            String code = row[0].toString();
            String type = row[2].toString();
            BigDecimal debit = money((BigDecimal) row[3]);
            BigDecimal credit = money((BigDecimal) row[4]);
            BigDecimal net = debit.subtract(credit);

            if ("REVENUE".equals(type) || "EXPENSE".equals(type) || CURRENT_YEAR_EARNINGS.equals(code)) {
                if (net.signum() == 0) continue;
                if ("REVENUE".equals(type) || CURRENT_YEAR_EARNINGS.equals(code)) {
                    netIncome = netIncome.add(net);
                } else {
                    netIncome = netIncome.add(net.negate());
                }
                if (CURRENT_YEAR_EARNINGS.equals(code)) currentYearEarnings = currentYearEarnings.add(net);
                if (net.signum() > 0) {
                    closing.add(new FinancePostingService.PostingLine(code, BigDecimal.ZERO, net, null, null, null, null, "Year-end close"));
                    closing.add(new FinancePostingService.PostingLine(RETAINED_EARNINGS, net, BigDecimal.ZERO, null, null, null, null, "Transfer to retained earnings"));
                } else {
                    BigDecimal amount = net.abs();
                    closing.add(new FinancePostingService.PostingLine(code, amount, BigDecimal.ZERO, null, null, null, null, "Year-end close"));
                    closing.add(new FinancePostingService.PostingLine(RETAINED_EARNINGS, BigDecimal.ZERO, amount, null, null, null, null, "Transfer to retained earnings"));
                }
            }
        }

        LocalDate yearEnd = LocalDate.of(fiscalYear, 12, 31);
        var closeDoc = postingService.postSystemBalancedAtDate(
            "YEAR_END_CLOSE", REF_TYPE, null, closeRef, "USD",
            "Fiscal year " + fiscalYear + " income statement close",
            closing, yearEnd.atTime(23,59,59));

        BigDecimal retainedEarningsOriginal = BigDecimal.ZERO;
        List<FinancePostingService.PostingLine> opening = new ArrayList<>();

        for (Object[] row : balances) {
            String code = row[0].toString();
            String type = row[2].toString();
            BigDecimal debit = money((BigDecimal) row[3]);
            BigDecimal credit = money((BigDecimal) row[4]);
            BigDecimal net = debit.subtract(credit);

            if (RETAINED_EARNINGS.equals(code)) {
                retainedEarningsOriginal = retainedEarningsOriginal.add(net);
                continue;
            }
            if ("REVENUE".equals(type) || "EXPENSE".equals(type) || CURRENT_YEAR_EARNINGS.equals(code)) continue;
            if (!("ASSET".equals(type) || "LIABILITY".equals(type) || "EQUITY".equals(type))) continue;
            addNet(opening, code, net, "Opening balance carried forward");
        }

        BigDecimal retainedAfterClose = retainedEarningsOriginal.add(netIncome);
        addNet(opening, RETAINED_EARNINGS, retainedAfterClose, "Opening retained earnings");

        LocalDate nextYearStart = LocalDate.of(fiscalYear + 1, 1, 1);
        var openDoc = postingService.postSystemBalancedAtDate(
            "YEAR_OPENING", REF_TYPE, null, openRef, "USD",
            "Opening balances for fiscal year " + (fiscalYear + 1),
            opening, nextYearStart.atStartOfDay());

        return new FiscalYearCloseResponse(fiscalYear, fiscalYear + 1, closeDoc.getDocumentNumber(), openDoc.getDocumentNumber(),
            netIncome.setScale(2, RoundingMode.HALF_UP), closing.size(), opening.size());
    }

    private void addNet(List<FinancePostingService.PostingLine> lines, String accountCode, BigDecimal net, String text) {
        if (net.signum() > 0) lines.add(new FinancePostingService.PostingLine(accountCode, net, BigDecimal.ZERO, null, null, null, null, text));
        else if (net.signum() < 0) lines.add(new FinancePostingService.PostingLine(accountCode, BigDecimal.ZERO, net.abs(), null, null, null, null, text));
    }

    private BigDecimal money(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2) : value.setScale(2, RoundingMode.HALF_UP);
    }
}
