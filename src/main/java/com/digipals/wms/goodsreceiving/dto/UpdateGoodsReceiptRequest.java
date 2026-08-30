package com.digipals.wms.goodsreceiving.dto;

import jakarta.validation.Valid;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class UpdateGoodsReceiptRequest {

    private String supplierDeliveryNote;

    private String remarks;

    /**
     * Receipt quantities are identified by the human-facing product SKU rather
     * than exposing a GRN line UUID in the endpoint.
     */
    @Valid
    private List<LineQuantityRequest> lines;

    @Data
    public static class LineQuantityRequest {
        private String sku;
        private BigDecimal receivedQuantity;
        private BigDecimal acceptedQuantity;
        private BigDecimal rejectedQuantity;
        private String remarks;
    }
}
