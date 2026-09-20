package com.digipals.wms.finance.dto;
import java.math.BigDecimal;
public record TaxReportLine(String taxCode,String description,BigDecimal rate,boolean withholding,String taxType,String jurisdiction,String inputOutput,BigDecimal taxableBase,BigDecimal taxAmount,BigDecimal recoverableAmount,BigDecimal netTax){}
