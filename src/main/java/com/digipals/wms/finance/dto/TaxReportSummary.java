package com.digipals.wms.finance.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public record TaxReportSummary(String companyCode,LocalDate periodStart,LocalDate periodEnd,BigDecimal outputTax,BigDecimal inputTax,BigDecimal recoverableInputTax,BigDecimal netTax,List<TaxReportLine> lines){}
