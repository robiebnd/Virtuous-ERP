package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record InternalOrderRequest(@NotBlank String orderCode, @NotBlank @Size(max=150) String name, @Size(max=500) String description, @Size(max=30) String costCenterCode, @Size(max=150) String responsiblePerson, @NotNull @DecimalMin("0.00") BigDecimal budgetAmount, @Pattern(regexp="[A-Za-z]{3}") String currency, LocalDate startDate, LocalDate endDate) {}