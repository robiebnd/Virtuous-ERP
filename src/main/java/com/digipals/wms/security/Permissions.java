package com.digipals.wms.security;

public final class Permissions {

    private Permissions() {
    }

    /*
     * ==========================================================
     * WAREHOUSES
     * ==========================================================
     */
    public static final String WAREHOUSE_CREATE = "WAREHOUSE_CREATE";
    public static final String WAREHOUSE_VIEW = "WAREHOUSE_VIEW";
    public static final String WAREHOUSE_UPDATE = "WAREHOUSE_UPDATE";
    public static final String WAREHOUSE_DELETE = "WAREHOUSE_DELETE";
    public static final String WAREHOUSE_ACTIVATE = "WAREHOUSE_ACTIVATE";

    /*
     * ==========================================================
     * PRODUCT CATEGORIES
     * ==========================================================
     */
    public static final String PRODUCT_CATEGORY_CREATE = "PRODUCT_CATEGORY_CREATE";
    public static final String PRODUCT_CATEGORY_VIEW = "PRODUCT_CATEGORY_VIEW";
    public static final String PRODUCT_CATEGORY_UPDATE = "PRODUCT_CATEGORY_UPDATE";
    public static final String PRODUCT_CATEGORY_DELETE = "PRODUCT_CATEGORY_DELETE";

    /*
     * ==========================================================
     * PRODUCTS
     * ==========================================================
     */
    public static final String PRODUCT_CREATE = "PRODUCT_CREATE";
    public static final String PRODUCT_VIEW = "PRODUCT_VIEW";
    public static final String PRODUCT_UPDATE = "PRODUCT_UPDATE";
    public static final String PRODUCT_DELETE = "PRODUCT_DELETE";
    public static final String PRODUCT_PRICE_UPDATE = "PRODUCT_PRICE_UPDATE";

    /*
     * ==========================================================
     * UNITS OF MEASURE
     * ==========================================================
     */
    public static final String UOM_CREATE = "UOM_CREATE";
    public static final String UOM_VIEW = "UOM_VIEW";
    public static final String UOM_UPDATE = "UOM_UPDATE";
    public static final String UOM_DELETE = "UOM_DELETE";

    /*
     * ==========================================================
     * SUPPLIERS
     * ==========================================================
     */
    public static final String SUPPLIER_CREATE = "SUPPLIER_CREATE";
    public static final String SUPPLIER_VIEW = "SUPPLIER_VIEW";
    public static final String SUPPLIER_UPDATE = "SUPPLIER_UPDATE";
    public static final String SUPPLIER_DELETE = "SUPPLIER_DELETE";
    public static final String SUPPLIER_BLACKLIST = "SUPPLIER_BLACKLIST";

    /*
     * ==========================================================
     * CUSTOMERS
     * ==========================================================
     */
    public static final String CUSTOMER_CREATE = "CUSTOMER_CREATE";
    public static final String CUSTOMER_VIEW = "CUSTOMER_VIEW";
    public static final String CUSTOMER_UPDATE = "CUSTOMER_UPDATE";
    public static final String CUSTOMER_DELETE = "CUSTOMER_DELETE";
    public static final String CUSTOMER_CREDIT_LIMIT = "CUSTOMER_CREDIT_LIMIT";

    /*
     * ==========================================================
     * PURCHASE REQUISITIONS
     * ==========================================================
     */
    public static final String PURCHASE_REQUISITION_CREATE = "PURCHASE_REQUISITION_CREATE";
    public static final String PURCHASE_REQUISITION_VIEW = "PURCHASE_REQUISITION_VIEW";
    public static final String PURCHASE_REQUISITION_UPDATE = "PURCHASE_REQUISITION_UPDATE";
    public static final String PURCHASE_REQUISITION_DELETE = "PURCHASE_REQUISITION_DELETE";
    public static final String PURCHASE_REQUISITION_SUBMIT = "PURCHASE_REQUISITION_SUBMIT";
    public static final String PURCHASE_REQUISITION_APPROVE = "PURCHASE_REQUISITION_APPROVE";
    public static final String PURCHASE_REQUISITION_CANCEL = "PURCHASE_REQUISITION_CANCEL";

    /*
     * ==========================================================
     * PURCHASE ORDERS
     * ==========================================================
     */
    public static final String PURCHASE_ORDER_CREATE = "PURCHASE_ORDER_CREATE";
    public static final String PURCHASE_ORDER_VIEW = "PURCHASE_ORDER_VIEW";
    public static final String PURCHASE_ORDER_UPDATE = "PURCHASE_ORDER_UPDATE";
    public static final String PURCHASE_ORDER_DELETE = "PURCHASE_ORDER_DELETE";
    public static final String PURCHASE_ORDER_APPROVE = "PURCHASE_ORDER_APPROVE";
    public static final String PURCHASE_ORDER_CANCEL = "PURCHASE_ORDER_CANCEL";
    public static final String PURCHASE_ORDER_PRINT = "PURCHASE_ORDER_PRINT";

    /*
     * ==========================================================
     * GOODS RECEIPTS
     * ==========================================================
     */
    public static final String GOODS_RECEIPT_CREATE = "GOODS_RECEIPT_CREATE";
    public static final String GOODS_RECEIPT_VIEW = "GOODS_RECEIPT_VIEW";
    public static final String GOODS_RECEIPT_UPDATE = "GOODS_RECEIPT_UPDATE";
    public static final String GOODS_RECEIPT_DELETE = "GOODS_RECEIPT_DELETE";
    public static final String GOODS_RECEIPT_APPROVE = "GOODS_RECEIPT_APPROVE";
    public static final String GOODS_RECEIPT_POST = "GOODS_RECEIPT_POST";
    public static final String GOODS_RECEIPT_CANCEL = "GOODS_RECEIPT_CANCEL";
    public static final String GOODS_RECEIPT_PRINT = "GOODS_RECEIPT_PRINT";

    /*
     * ==========================================================
     * INVENTORY
     * ==========================================================
     */
    public static final String INVENTORY_VIEW = "INVENTORY_VIEW";
    public static final String INVENTORY_ADJUST = "INVENTORY_ADJUST";
    public static final String INVENTORY_TRANSACTION_VIEW = "INVENTORY_TRANSACTION_VIEW";

    /*
     * ==========================================================
     * STOCK TRANSFERS
     * ==========================================================
     */
    public static final String STOCK_TRANSFER_CREATE = "STOCK_TRANSFER_CREATE";
    public static final String STOCK_TRANSFER_VIEW = "STOCK_TRANSFER_VIEW";
    public static final String STOCK_TRANSFER_UPDATE = "STOCK_TRANSFER_UPDATE";
    public static final String STOCK_TRANSFER_DELETE = "STOCK_TRANSFER_DELETE";
    public static final String STOCK_TRANSFER_APPROVE = "STOCK_TRANSFER_APPROVE";
    public static final String STOCK_TRANSFER_POST = "STOCK_TRANSFER_POST";
    public static final String STOCK_TRANSFER_RECEIVE = "STOCK_TRANSFER_RECEIVE";
    public static final String STOCK_TRANSFER_CANCEL = "STOCK_TRANSFER_CANCEL";

    /*
     * ==========================================================
     * STOCK ADJUSTMENTS
     * ==========================================================
     */
    public static final String STOCK_ADJUSTMENT_CREATE = "STOCK_ADJUSTMENT_CREATE";
    public static final String STOCK_ADJUSTMENT_VIEW = "STOCK_ADJUSTMENT_VIEW";
    public static final String STOCK_ADJUSTMENT_UPDATE = "STOCK_ADJUSTMENT_UPDATE";
    public static final String STOCK_ADJUSTMENT_DELETE = "STOCK_ADJUSTMENT_DELETE";
    public static final String STOCK_ADJUSTMENT_APPROVE = "STOCK_ADJUSTMENT_APPROVE";
    public static final String STOCK_ADJUSTMENT_POST = "STOCK_ADJUSTMENT_POST";
    public static final String STOCK_ADJUSTMENT_CANCEL = "STOCK_ADJUSTMENT_CANCEL";

    /*
     * ==========================================================
     * STOCK COUNTS
     * ==========================================================
     */
    public static final String STOCK_COUNT_CREATE = "STOCK_COUNT_CREATE";
    public static final String STOCK_COUNT_VIEW = "STOCK_COUNT_VIEW";
    public static final String STOCK_COUNT_UPDATE = "STOCK_COUNT_UPDATE";
    public static final String STOCK_COUNT_DELETE = "STOCK_COUNT_DELETE";
    public static final String STOCK_COUNT_APPROVE = "STOCK_COUNT_APPROVE";
    public static final String STOCK_COUNT_POST = "STOCK_COUNT_POST";
    public static final String STOCK_COUNT_CANCEL = "STOCK_COUNT_CANCEL";
    public static final String STOCK_COUNT_FINALIZE = "STOCK_COUNT_FINALIZE";

    /*
     * ==========================================================
     * SALES ORDERS
     * ==========================================================
     */
    public static final String SALES_ORDER_CREATE = "SALES_ORDER_CREATE";
    public static final String SALES_ORDER_VIEW = "SALES_ORDER_VIEW";
    public static final String SALES_ORDER_UPDATE = "SALES_ORDER_UPDATE";
    public static final String SALES_ORDER_DELETE = "SALES_ORDER_DELETE";
    public static final String SALES_ORDER_APPROVE = "SALES_ORDER_APPROVE";
    public static final String SALES_ORDER_POST = "SALES_ORDER_POST";
    public static final String SALES_ORDER_CANCEL = "SALES_ORDER_CANCEL";
    public static final String SALES_ORDER_PRINT = "SALES_ORDER_PRINT";

    /*
     * ==========================================================
     * DISPATCH NOTES
     * ==========================================================
     */
    public static final String DISPATCH_CREATE = "DISPATCH_CREATE";
    public static final String DISPATCH_VIEW = "DISPATCH_VIEW";
    public static final String DISPATCH_UPDATE = "DISPATCH_UPDATE";
    public static final String DISPATCH_DELETE = "DISPATCH_DELETE";
    public static final String DISPATCH_APPROVE = "DISPATCH_APPROVE";
    public static final String DISPATCH_POST = "DISPATCH_POST";
    public static final String DISPATCH_CANCEL = "DISPATCH_CANCEL";
    public static final String DISPATCH_PRINT = "DISPATCH_PRINT";

    /*
     * ==========================================================
     * USERS
     * ==========================================================
     */
    public static final String USER_CREATE = "USER_CREATE";
    public static final String USER_VIEW = "USER_VIEW";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";
    public static final String USER_LOCK = "USER_LOCK";
    public static final String USER_RESET_PASSWORD = "USER_RESET_PASSWORD";

    /*
     * ==========================================================
     * ROLES
     * ==========================================================
     */
    public static final String ROLE_CREATE = "ROLE_CREATE";
    public static final String ROLE_VIEW = "ROLE_VIEW";
    public static final String ROLE_UPDATE = "ROLE_UPDATE";
    public static final String ROLE_DELETE = "ROLE_DELETE";
    public static final String ROLE_ASSIGN = "ROLE_ASSIGN";

    /*
     * ==========================================================
     * PERMISSIONS
     * ==========================================================
     */
    public static final String PERMISSION_CREATE = "PERMISSION_CREATE";
    public static final String PERMISSION_VIEW = "PERMISSION_VIEW";
    public static final String PERMISSION_UPDATE = "PERMISSION_UPDATE";
    public static final String PERMISSION_DELETE = "PERMISSION_DELETE";

    /*
     * ==========================================================
     * REPORTS
     * ==========================================================
     */
    public static final String REPORT_VIEW = "REPORT_VIEW";
    public static final String REPORT_EXPORT = "REPORT_EXPORT";

    /*
     * ==========================================================
     * DASHBOARD
     * ==========================================================
     */
    public static final String DASHBOARD_VIEW = "DASHBOARD_VIEW";

    /*
     * ==========================================================
     * AUDIT TRAIL
     * ==========================================================
     */
    public static final String AUDIT_VIEW = "AUDIT_VIEW";
    public static final String AUDIT_EXPORT = "AUDIT_EXPORT";
}