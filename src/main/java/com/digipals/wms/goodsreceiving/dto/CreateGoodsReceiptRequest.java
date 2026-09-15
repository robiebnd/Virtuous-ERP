package com.digipals.wms.goodsreceiving.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateGoodsReceiptRequest {

    /** Internal identifier retained for integrations/backwards compatibility. */
    private UUID purchaseOrderId;

    /** Human-facing Purchase Order number preferred by the frontend. */
    private String purchaseOrderNumber;

    private String supplierDeliveryNote;

    private String remarks;
}
