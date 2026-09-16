package com.digipals.wms.finance.service;

import com.digipals.wms.finance.dto.InventoryGlReconciliationResponse;
import com.digipals.wms.finance.dto.InventoryValuationResponse;
import com.digipals.wms.finance.dto.TrialBalanceLine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryGlReconciliationService {

    private static final String INVENTORY_ACCOUNT = "110000";
    private static final BigDecimal TOLERANCE = new BigDecimal("0.01");

    private final InventoryValuationService inventoryValuationService;
    private final FinanceQueryService financeQueryService;

    public InventoryGlReconciliationResponse reconcile() {
        BigDecimal valuation = inventoryValuationService.valuation().stream()
                .map(InventoryValuationResponse::inventoryValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal glBalance = financeQueryService.trialBalance().stream()
                .filter(line -> INVENTORY_ACCOUNT.equals(line.accountCode()))
                .map(this::netBalance)
                .findFirst()
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal variance = valuation.subtract(glBalance).setScale(2, RoundingMode.HALF_UP);
        return new InventoryGlReconciliationResponse(valuation, glBalance, variance, variance.abs().compareTo(TOLERANCE) <= 0);
    }

    private BigDecimal netBalance(TrialBalanceLine line) {
        return line.debitBalance().subtract(line.creditBalance());
    }
}
