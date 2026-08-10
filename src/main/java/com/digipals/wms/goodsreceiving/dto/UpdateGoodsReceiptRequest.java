package com.digipals.wms.goodsreceiving.dto;

import lombok.Data;

@Data
public class UpdateGoodsReceiptRequest {

    private String supplierDeliveryNote;

    private String remarks;
}
