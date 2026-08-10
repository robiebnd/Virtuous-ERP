package com.digipals.wms.goodsreceiving.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateGoodsReceiptRequest {

    @NotNull(message = "Purchase Order is required")
    private UUID purchaseOrderId;

    private String supplierDeliveryNote;

    private String remarks;
}
