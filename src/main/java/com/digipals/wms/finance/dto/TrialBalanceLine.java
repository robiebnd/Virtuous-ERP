package com.digipals.wms.finance.dto;

import java.math.BigDecimal;

public record TrialBalanceLine(String accountCode, String accountName, BigDecimal debitBalance, BigDecimal creditBalance) {}
