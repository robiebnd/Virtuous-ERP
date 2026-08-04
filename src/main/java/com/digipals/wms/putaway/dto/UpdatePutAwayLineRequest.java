package com.digipals.wms.putaway.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdatePutAwayLineRequest {

    @NotNull(message = "Destination bin is required")
    private UUID toBinId;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01")
    private BigDecimal quantity;

    private String remarks;
}