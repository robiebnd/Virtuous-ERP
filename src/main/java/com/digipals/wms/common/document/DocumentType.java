package com.digipals.wms.common.document;

public enum DocumentType {

    // Procurement
    PURCHASE_REQUISITION("PR"),
    PURCHASE_ORDER("PO"),
    GOODS_RECEIPT("GRN"),

    // Inventory Control
    STOCK_ADJUSTMENT("SA"),
    STOCK_COUNT("SC"),

    // Warehouse Movements
    STOCK_TRANSFER("ST"),
    BIN_TRANSFER("BT"),

    // Warehouse Operations
    PUTAWAY("PA"),
    PICK_LIST("PL"),
    PICK_CONFIRMATION("PC"),
    REPLENISHMENT("RP"),
    GOODS_ISSUE("GI"),
    GOODS_RETURN("RT");

    private final String prefix;

    DocumentType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}