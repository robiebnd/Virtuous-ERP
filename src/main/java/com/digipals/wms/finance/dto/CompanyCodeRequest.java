package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.NotBlank;
public record CompanyCodeRequest(@NotBlank String companyCode,@NotBlank String companyName,@NotBlank String countryCode,@NotBlank String functionalCurrency,String fiscalYearVariant,String reportingCurrency){}