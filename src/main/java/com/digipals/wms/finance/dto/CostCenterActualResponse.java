package com.digipals.wms.finance.dto;
import java.math.BigDecimal;
public record CostCenterActualResponse(String costCenterCode, BigDecimal actualAmount, BigDecimal debit, BigDecimal credit, BigDecimal budgetAmount, BigDecimal variance) {}