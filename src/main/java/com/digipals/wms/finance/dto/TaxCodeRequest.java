package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalDate;
public record TaxCodeRequest(@NotBlank String companyCode,@NotBlank String taxCode,@NotBlank String description,@NotNull @DecimalMin("0.00") @DecimalMax("100.00") BigDecimal rate,String inputAccountCode,String outputAccountCode,boolean withholding,String taxType,String jurisdiction,LocalDate effectiveFrom,LocalDate effectiveTo,@DecimalMin("0.00") @DecimalMax("100.00") BigDecimal recoverablePercent,String taxAccountCode){}
