package com.digipals.wms.finance.dto;
import java.math.BigDecimal;
public record InternalOrderActualResponse(String orderCode, BigDecimal actualAmount, BigDecimal budgetAmount, BigDecimal variance) {}