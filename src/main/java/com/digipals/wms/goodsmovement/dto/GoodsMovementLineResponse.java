package com.digipals.wms.goodsmovement.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsMovementLineResponse {

    private UUID id;

    /*
     * Product
     */
    private UUID productId;

    private String sku;

    private String productName;

    /*
     * Source Bin
     */
    private UUID fromBinId;

    private String fromBinCode;

    private String fromBinName;

    /*
     * Destination Bin
     */
    private UUID toBinId;

    private String toBinCode;

    private String toBinName;

    /*
     * Movement
     */
    private BigDecimal quantity;

    private BigDecimal unitCost;

    private String remarks;
}
