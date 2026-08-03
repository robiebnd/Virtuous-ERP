package com.digipals.wms.bintransfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBinTransferLineRequest {

    @NotNull
    private UUID binTransferId;

    @NotNull
    private UUID productId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal quantity;

    private String remarks;
}