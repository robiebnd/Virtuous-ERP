package com.digipals.wms.goodsmovement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePutAwayRequest {

    @NotBlank(message = "Warehouse code is required")
    private String warehouseCode;

    @NotBlank(message = "Reference number is required")
    private String referenceNumber;

    @NotBlank(message = "Reference type is required")
    private String referenceType;

    private String remarks;

    @Valid
    @NotEmpty(message = "At least one put-away line is required")
    @Builder.Default
    private List<PutAwayLineRequest> lines = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PutAwayLineRequest {
        @NotBlank(message = "SKU is required")
        private String sku;

        @NotBlank(message = "Source bin code is required")
        private String fromBinCode;

        @NotBlank(message = "Destination bin code is required")
        private String toBinCode;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than zero")
        private BigDecimal quantity;

        private BigDecimal unitCost;
        private String remarks;
    }
}
