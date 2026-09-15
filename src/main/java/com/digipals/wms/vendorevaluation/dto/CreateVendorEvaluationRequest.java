package com.digipals.wms.vendorevaluation.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateVendorEvaluationRequest {
    @NotNull private UUID supplierId;
    @NotNull private UUID purchaseOrderId;
    @NotNull @DecimalMin("0") @DecimalMax("100") private BigDecimal priceScore;
    @NotNull @DecimalMin("0") @DecimalMax("100") private BigDecimal qualityScore;
    @NotNull @DecimalMin("0") @DecimalMax("100") private BigDecimal deliveryScore;
    @NotNull @DecimalMin("0") @DecimalMax("100") private BigDecimal serviceScore;
    private String remarks;
}
