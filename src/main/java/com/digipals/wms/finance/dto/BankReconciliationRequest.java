package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.NotBlank;
public record BankReconciliationRequest(@NotBlank String bankAccountNumber,@NotBlank String transactionNumber,@NotBlank String accountingDocumentNumber){}