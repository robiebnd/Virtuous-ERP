package com.digipals.wms.purchasinginforecord.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PurchasingInfoRecordRequest {

    @NotNull(message = "Supplier product identifier is required")
    private UUID supplierProductIdentifierId;

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    @NotBlank(message = "Currency is required")
    private String currency;

    @DecimalMin(value = "0.0", inclusive = true, message = "Last purchase price cannot be negative")
    private BigDecimal lastPurchasePrice;

    @DecimalMin(value = "0.0001", message = "Standard order quantity must be greater than zero")
    private BigDecimal standardOrderQuantity;

    private Integer plannedDeliveryDays;
    private LocalDate validFrom;
    private LocalDate validTo;
    private Boolean regularSupplier = false;
    private Boolean automaticSourcing = false;
}
