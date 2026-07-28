package com.digipals.wms.goodsreceiving.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateGoodsReceiptRequest {

    @NotNull(message = "Purchase Order is required")
    private UUID purchaseOrderId;

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    private String supplierDeliveryNote;

    private String remarks;
}