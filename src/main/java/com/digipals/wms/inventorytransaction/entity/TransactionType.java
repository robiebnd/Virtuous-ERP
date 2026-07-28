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

    WRITE_OFF

}