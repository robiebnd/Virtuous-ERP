package com.digipals.wms.inventorytransaction.entity;

import jakarta.transaction.Transactional;



@Transactional
public enum TransactionType {

    GOODS_RECEIPT,

    PURCHASE_RECEIPT,

    TRANSFER_IN,

    TRANSFER_OUT,

    ADJUSTMENT,

    STOCK_COUNT,

    SALE,

    CUSTOMER_RETURN,

    SUPPLIER_RETURN,

    WRITE_OFF,
    PUTAWAY,
    PICK,

    REPLENISHMENT_OUT,
    REPLENISHMENT_IN,

    ADJUSTMENT_IN,
    ADJUSTMENT_OUT,

    COUNT,

    RETURN_IN,
    RETURN_OUT


}