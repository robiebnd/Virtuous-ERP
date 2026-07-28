-- ==========================================================
-- V4__seed_role_permissions.sql
-- ROLE → PERMISSION MAPPING
-- ==========================================================

--------------------------------------------------------------
-- SYSTEM ADMIN
-- Full Access
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SYSTEM_ADMIN'
ON CONFLICT DO NOTHING;

--------------------------------------------------------------
-- WAREHOUSE MANAGER
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
FROM roles r
JOIN permissions p
ON p.code IN
(
'WAREHOUSE_VIEW',
'WAREHOUSE_CREATE',
'WAREHOUSE_UPDATE',

'PRODUCT_VIEW',

'CATEGORY_VIEW',

'INVENTORY_VIEW',
'INVENTORY_ADJUST',
'INVENTORY_TRANSFER',
'INVENTORY_VALUATION',

'GOODS_RECEIPT_VIEW',
'GOODS_RECEIPT_CREATE',
'GOODS_RECEIPT_POST',

'STOCK_TRANSFER_VIEW',
'STOCK_TRANSFER_CREATE',
'STOCK_TRANSFER_APPROVE',
'STOCK_TRANSFER_RECEIVE',

'STOCK_COUNT_VIEW',
'STOCK_COUNT_CREATE',
'STOCK_COUNT_APPROVE',

'STOCK_ADJUSTMENT_VIEW',
'STOCK_ADJUSTMENT_CREATE',
'STOCK_ADJUSTMENT_APPROVE',

'REPORT_VIEW',

'DASHBOARD_VIEW'
)
WHERE r.name='WAREHOUSE_MANAGER'
ON CONFLICT DO NOTHING;

--------------------------------------------------------------
-- WAREHOUSE CLERK
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
FROM roles r
JOIN permissions p
ON p.code IN
(
'WAREHOUSE_VIEW',

'PRODUCT_VIEW',

'CATEGORY_VIEW',

'INVENTORY_VIEW',

'GOODS_RECEIPT_VIEW',
'GOODS_RECEIPT_CREATE',

'STOCK_TRANSFER_VIEW',

'STOCK_COUNT_VIEW',
'STOCK_COUNT_CREATE'
)
WHERE r.name='WAREHOUSE_CLERK'
ON CONFLICT DO NOTHING;

--------------------------------------------------------------
-- PURCHASING OFFICER
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
FROM roles r
JOIN permissions p
ON p.code IN
(
'SUPPLIER_VIEW',
'SUPPLIER_CREATE',
'SUPPLIER_UPDATE',

'PRODUCT_VIEW',

'PURCHASE_REQUISITION_VIEW',
'PURCHASE_REQUISITION_CREATE',
'PURCHASE_REQUISITION_UPDATE',
'PURCHASE_REQUISITION_SUBMIT',

'PURCHASE_ORDER_VIEW',
'PURCHASE_ORDER_CREATE',
'PURCHASE_ORDER_UPDATE',

'GOODS_RECEIPT_VIEW',
'GOODS_RECEIPT_CREATE',

'REPORT_VIEW',

'DASHBOARD_VIEW'
)
WHERE r.name='PURCHASING_OFFICER'
ON CONFLICT DO NOTHING;

--------------------------------------------------------------
-- PROCUREMENT MANAGER
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
FROM roles r
JOIN permissions p
ON p.code IN
(
'SUPPLIER_VIEW',
'SUPPLIER_CREATE',
'SUPPLIER_UPDATE',

'PURCHASE_REQUISITION_VIEW',
'PURCHASE_REQUISITION_APPROVE',
'PURCHASE_REQUISITION_REJECT',
'PURCHASE_REQUISITION_CANCEL',

'PURCHASE_ORDER_VIEW',
'PURCHASE_ORDER_CREATE',
'PURCHASE_ORDER_APPROVE',
'PURCHASE_ORDER_CANCEL',
'PURCHASE_ORDER_CLOSE',

'GOODS_RECEIPT_VIEW',
'GOODS_RECEIPT_POST',

'REPORT_VIEW',
'DASHBOARD_VIEW',

'AI_PROCUREMENT',
'AI_FORECASTING'
)
WHERE r.name='PROCUREMENT_MANAGER'
ON CONFLICT DO NOTHING;

--------------------------------------------------------------
-- INVENTORY CONTROLLER
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
FROM roles r
JOIN permissions p
ON p.code IN
(
'INVENTORY_VIEW',
'INVENTORY_ADJUST',
'INVENTORY_TRANSFER',
'INVENTORY_VALUATION',

'GOODS_RECEIPT_VIEW',
'GOODS_RECEIPT_POST',

'STOCK_TRANSFER_VIEW',
'STOCK_TRANSFER_CREATE',
'STOCK_TRANSFER_APPROVE',
'STOCK_TRANSFER_RECEIVE',

'STOCK_COUNT_VIEW',
'STOCK_COUNT_CREATE',
'STOCK_COUNT_APPROVE',

'STOCK_ADJUSTMENT_VIEW',
'STOCK_ADJUSTMENT_CREATE',
'STOCK_ADJUSTMENT_APPROVE',

'REPORT_VIEW',

'DASHBOARD_VIEW'
)
WHERE r.name='INVENTORY_CONTROLLER'
ON CONFLICT DO NOTHING;

--------------------------------------------------------------
-- SALES MANAGER
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
FROM roles r
JOIN permissions p
ON p.code IN
(
'CUSTOMER_VIEW',
'CUSTOMER_CREATE',
'CUSTOMER_UPDATE',

'INVOICE_VIEW',
'INVOICE_CREATE',
'INVOICE_UPDATE',
'INVOICE_APPROVE',

'PAYMENT_VIEW',
'PAYMENT_RECEIVE',

'REPORT_VIEW',

'DASHBOARD_VIEW'
)
WHERE r.name='SALES_MANAGER'
ON CONFLICT DO NOTHING;

--------------------------------------------------------------
-- FINANCE MANAGER
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
   active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
   TRUE,
   0
FROM roles r
JOIN permissions p
ON p.code IN
(
'PAYMENT_VIEW',
'PAYMENT_RECEIVE',
'REPORT_VIEW',
'REPORT_EXPORT',
'DASHBOARD_VIEW',
'SYSTEM_SETTINGS'
)
WHERE r.name='FINANCE_MANAGER'
ON CONFLICT DO NOTHING;

--------------------------------------------------------------
-- AUDITOR
-- Read Only
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
FROM roles r
JOIN permissions p
ON p.code LIKE '%VIEW'
WHERE r.name='AUDITOR'
ON CONFLICT DO NOTHING;