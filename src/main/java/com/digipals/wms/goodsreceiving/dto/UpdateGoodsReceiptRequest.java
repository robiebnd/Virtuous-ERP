package com.digipals.wms.goodsreceiving.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateGoodsReceiptRequest {

    @NotNull(message = "Warehouse is required")
    private UUID warehouseId;

    private String supplierDeliveryNote;

    private String remarks;
}