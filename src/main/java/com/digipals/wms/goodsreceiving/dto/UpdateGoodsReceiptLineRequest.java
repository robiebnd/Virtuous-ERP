package com.digipals.wms.goodsreceiving.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateGoodsReceiptLineRequest {

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal receivedQuantity;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal acceptedQuantity;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal rejectedQuantity;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal unitCost;

    private String remarks;
}