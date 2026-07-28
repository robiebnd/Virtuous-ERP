package com.digipals.wms.common.document;


public enum DocumentType {

    PURCHASE_ORDER("PO"),

    GOODS_RECEIPT("GRN"),

    STOCK_TRANSFER("TR"),

    STOCK_ADJUSTMENT("SA"),

    STOCK_COUNT("SC"),

    PURCHASE_REQUISITION("PR");

    private final String prefix;

    DocumentType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}