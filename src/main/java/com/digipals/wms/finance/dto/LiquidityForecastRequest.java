package com.digipals.wms.finance.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public record LiquidityForecastRequest(@NotNull LocalDate forecastDate,@NotBlank String category,String description,@NotNull @DecimalMin("0.00") BigDecimal expectedInflow,@NotNull @DecimalMin("0.00") BigDecimal expectedOutflow,@Pattern(regexp="[A-Za-z]{3}") String currency){}