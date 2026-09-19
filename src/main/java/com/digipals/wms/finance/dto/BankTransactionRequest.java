package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record BankTransactionRequest(@NotBlank String bankAccountNumber,@NotBlank String transactionNumber,@NotNull LocalDate transactionDate,LocalDate valueDate,@NotNull @DecimalMin("0.01") BigDecimal amount,@NotBlank String direction,String reference,String description){}