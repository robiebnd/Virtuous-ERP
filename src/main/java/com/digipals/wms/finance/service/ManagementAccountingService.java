package com.digipals.wms.finance.service;

import com.digipals.wms.common.exception.InvalidWorkflowException;
import com.digipals.wms.finance.dto.*;
import com.digipals.wms.finance.entity.*;
import com.digipals.wms.finance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ManagementAccountingService {
    private final CostCenterRepository costCenters;
    private final CostCenterBudgetRepository budgets;
    private final InternalOrderRepository internalOrders;
    private final AccountingLineRepository accountingLines;
    private final GlAccountRepository glAccounts;
    private final FinancePostingService postingService;

    @Transactional(readOnly = true)
    public List<CostCenterActualResponse> costCenterActuals(int fiscalYear) {
        Map<String, CostCenterBudget> budgetByCode = budgets.findByFiscalYearOrderByCostCenterCodeAsc(fiscalYear).stream()
                .collect(Collectors.toMap(CostCenterBudget::getCostCenterCode, Function.identity()));
        Map<String, Object[]> actualByCode = accountingLines.costCenterActuals().stream()
                .collect(Collectors.toMap(x -> (String)x[0], Function.identity()));
        return costCenters.findByActiveTrueOrderByCodeAsc().stream().map(cc -> {
            Object[] row = actualByCode.get(cc.getCode());
            BigDecimal debit = row == null ? BigDecimal.ZERO : bd(row[1]);
            BigDecimal credit = row == null ? BigDecimal.ZERO : bd(row[2]);
            BigDecimal actual = debit.subtract(credit).setScale(2, RoundingMode.HALF_UP);
            BigDecimal budget = budgetByCode.containsKey(cc.getCode()) ? bd(budgetByCode.get(cc.getCode()).getBudgetAmount()) : BigDecimal.ZERO;
            return new CostCenterActualResponse(cc.getCode(), actual, debit, credit, budget, budget.subtract(actual).setScale(2, RoundingMode.HALF_UP));
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<InternalOrderActualResponse> internalOrderActuals() {
        Map<String, BigDecimal> actuals = accountingLines.internalOrderActuals().stream()
                .collect(Collectors.toMap(x -> (String)x[0], x -> bd(x[1]).subtract(bd(x[2])).setScale(2, RoundingMode.HALF_UP)));
        return internalOrders.findAll().stream().sorted(Comparator.comparing(InternalOrder::getOrderCode)).map(o -> {
            BigDecimal actual = actuals.getOrDefault(o.getOrderCode(), BigDecimal.ZERO);
            BigDecimal budget = bd(o.getBudgetAmount());
            return new InternalOrderActualResponse(o.getOrderCode(), actual, budget, budget.subtract(actual).setScale(2, RoundingMode.HALF_UP));
        }).toList();
    }

    public CostCenterBudget saveBudget(CostCenterBudgetRequest request) {
        String code = normalizeCode(request.costCenterCode());
        CostCenter cc = costCenters.findByCode(code).filter(x -> Boolean.TRUE.equals(x.getActive()))
                .orElseThrow(() -> new InvalidWorkflowException("Active cost center not found: " + code));
        CostCenterBudget budget = budgets.findByCostCenterCodeAndFiscalYear(code, request.fiscalYear()).orElseGet(CostCenterBudget::new);
        budget.setCostCenterCode(cc.getCode());
        budget.setFiscalYear(request.fiscalYear());
        budget.setBudgetAmount(money(request.budgetAmount()));
        budget.setCurrency(normalizeCurrency(request.currency()));
        budget.setDescription(request.description());
        budget.setActive(true);
        return budgets.save(budget);
    }

    public InternalOrder createInternalOrder(InternalOrderRequest request) {
        String code = normalizeCode(request.orderCode());
        if (internalOrders.findByOrderCode(code).isPresent()) throw new InvalidWorkflowException("Internal order already exists: " + code);
        if (request.startDate() != null && request.endDate() != null && request.endDate().isBefore(request.startDate()))
            throw new InvalidWorkflowException("Internal order end date cannot be before start date.");
        String cc = request.costCenterCode() == null ? null : normalizeCode(request.costCenterCode());
        if (cc != null) costCenters.findByCode(cc).filter(x -> Boolean.TRUE.equals(x.getActive()))
                .orElseThrow(() -> new InvalidWorkflowException("Active cost center not found: " + cc));
        InternalOrder order = InternalOrder.builder().orderCode(code).name(request.name().trim()).description(request.description())
                .costCenterCode(cc).responsiblePerson(request.responsiblePerson()).budgetAmount(money(request.budgetAmount()))
                .currency(normalizeCurrency(request.currency())).status("OPEN").startDate(request.startDate()).endDate(request.endDate()).active(true).build();
        return internalOrders.save(order);
    }

    public AccountingDocument allocate(CostCenterAllocationRequest request) {
        String source = normalizeCode(request.sourceCostCenter());
        String target = normalizeCode(request.targetCostCenter());
        if (source.equals(target)) throw new InvalidWorkflowException("Source and target cost centers must be different.");
        validateCostCenter(source); validateCostCenter(target);
        String account = request.expenseAccountCode().trim();
        GlAccount gl = glAccounts.findByAccountCode(account).orElseThrow(() -> new InvalidWorkflowException("GL account not configured: " + account));
        if (!"EXPENSE".equalsIgnoreCase(gl.getAccountType())) throw new InvalidWorkflowException("Cost center allocations require an expense GL account.");
        BigDecimal amount = money(request.amount());
        UUID referenceId = UUID.nameUUIDFromBytes(("CC_ALLOC:" + request.referenceNumber().trim()).getBytes(StandardCharsets.UTF_8));
        return postingService.postBalanced("COST_CENTER_ALLOCATION", "COST_CENTER_ALLOCATION", referenceId,
                request.referenceNumber().trim(), "USD",
                request.description() == null ? "Cost center allocation" : request.description().trim(),
                List.of(
                        new FinancePostingService.PostingLine(account, amount, BigDecimal.ZERO, target, null, null, null, "Cost center allocation to " + target),
                        new FinancePostingService.PostingLine(account, BigDecimal.ZERO, amount, source, null, null, null, "Cost center allocation from " + source)
                ));
    }

    private void validateCostCenter(String code) {
        costCenters.findByCode(code).filter(x -> Boolean.TRUE.equals(x.getActive()))
                .orElseThrow(() -> new InvalidWorkflowException("Active cost center not found: " + code));
    }
    private String normalizeCode(String value) { return value == null ? null : value.trim().toUpperCase(Locale.ROOT); }
    private String normalizeCurrency(String value) { String x = value == null || value.isBlank() ? "USD" : value.trim().toUpperCase(Locale.ROOT); if (!x.matches("[A-Z]{3}")) throw new InvalidWorkflowException("Currency must be a 3-letter ISO code."); return x; }
    private BigDecimal money(BigDecimal value) { if (value == null || value.compareTo(BigDecimal.ZERO) < 0) throw new InvalidWorkflowException("Amount must be zero or greater."); return value.setScale(2, RoundingMode.HALF_UP); }
    private BigDecimal bd(Object value) { return value == null ? BigDecimal.ZERO : new BigDecimal(value.toString()); }
}