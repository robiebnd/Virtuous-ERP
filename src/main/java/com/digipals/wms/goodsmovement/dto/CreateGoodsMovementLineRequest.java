package com.digipals.wms.goodsmovement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGoodsMovementLineRequest {

    @NotNull(message = "Product is required")
    private UUID productId;

    /**
     *` Source bin.
     *
     * Required for movements such as:
     * BIN_TRANSFER
     * PUT_AWAY
     * STOCK_TRANSFER
     * PICK
     *
     * Optional for stock coming into the warehouse.
     */
    private UUID fromBinId;

    /**
     * Destination bin.
     *
     * Required for movements such as:
     * GOODS_RECEIPT
     * PUT_AWAY
     * BIN_TRANSFER
     * STOCK_TRANSFER
     * CUSTOMER_RETURN
     */
    private UUID toBinId;

    @NotNull(message = "Quantity is required"
    )
    @DecimalMin(value = "0.01",
            message = "Quantity must be greater than zero"
    )
    private BigDecimal quantity;

    private BigDecimal unitCost;

    private String remarks;
}
