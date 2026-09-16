package com.digipals.wms.finance.service;

import com.digipals.wms.finance.dto.TrialBalanceLine;
import com.digipals.wms.finance.repository.AccountingLineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinanceQueryService {
    private final AccountingLineRepository accountingLineRepository;

    public List<TrialBalanceLine> trialBalance() {
        return accountingLineRepository.trialBalance().stream().map(row -> {
            BigDecimal debit = (BigDecimal) row[2];
            BigDecimal credit = (BigDecimal) row[3];
            BigDecimal net = debit.subtract(credit);
            return new TrialBalanceLine(row[0].toString(), row[1].toString(), net.max(BigDecimal.ZERO), net.negate().max(BigDecimal.ZERO));
        }).toList();
    }
}
