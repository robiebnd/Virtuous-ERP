-- ==========================================================
-- V3__seed_permissions.sql
-- ERP PERMISSIONS
-- ==========================================================

--------------------------------------------------------------
-- WAREHOUSE
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('WAREHOUSE_VIEW','View Warehouses','View warehouses'),
('WAREHOUSE_CREATE','Create Warehouse','Create warehouses'),
('WAREHOUSE_UPDATE','Update Warehouse','Update warehouses'),
('WAREHOUSE_DELETE','Delete Warehouse','Delete warehouses')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- PRODUCTS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('PRODUCT_VIEW','View Products','View products'),
('PRODUCT_CREATE','Create Product','Create products'),
('PRODUCT_UPDATE','Update Product','Update products'),
('PRODUCT_DELETE','Delete Product','Delete products')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- CATEGORIES
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('CATEGORY_VIEW','View Categories','View categories'),
('CATEGORY_CREATE','Create Category','Create categories'),
('CATEGORY_UPDATE','Update Category','Update categories'),
('CATEGORY_DELETE','Delete Category','Delete categories')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- SUPPLIERS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('SUPPLIER_VIEW','View Suppliers','View suppliers'),
('SUPPLIER_CREATE','Create Supplier','Create suppliers'),
('SUPPLIER_UPDATE','Update Supplier','Update suppliers'),
('SUPPLIER_DELETE','Delete Supplier','Delete suppliers')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- USERS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('USER_VIEW','View Users','View users'),
('USER_CREATE','Create User','Create users'),
('USER_UPDATE','Update User','Update users'),
('USER_DELETE','Delete User','Delete users')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- ROLES
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('ROLE_VIEW','View Roles','View roles'),
('ROLE_CREATE','Create Role','Create roles'),
('ROLE_UPDATE','Update Role','Update roles'),
('ROLE_DELETE','Delete Role','Delete roles')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- PERMISSIONS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('PERMISSION_VIEW','View Permissions','View permissions'),
('PERMISSION_ASSIGN','Assign Permissions','Assign permissions')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- PURCHASE REQUISITIONS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('PURCHASE_REQUISITION_VIEW','View Purchase Requisitions','View requisitions'),
('PURCHASE_REQUISITION_CREATE','Create Purchase Requisition','Create requisitions'),
('PURCHASE_REQUISITION_UPDATE','Update Purchase Requisition','Update requisitions'),
('PURCHASE_REQUISITION_SUBMIT','Submit Purchase Requisition','Submit requisitions'),
('PURCHASE_REQUISITION_APPROVE','Approve Purchase Requisition','Approve requisitions'),
('PURCHASE_REQUISITION_REJECT','Reject Purchase Requisition','Reject requisitions'),
('PURCHASE_REQUISITION_CANCEL','Cancel Purchase Requisition','Cancel requisitions')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- PURCHASE ORDERS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('PURCHASE_ORDER_VIEW','View Purchase Orders','View purchase orders'),
('PURCHASE_ORDER_CREATE','Create Purchase Order','Create purchase orders'),
('PURCHASE_ORDER_UPDATE','Update Purchase Order','Update purchase orders'),
('PURCHASE_ORDER_APPROVE','Approve Purchase Order','Approve purchase orders'),
('PURCHASE_ORDER_CANCEL','Cancel Purchase Order','Cancel purchase orders'),
('PURCHASE_ORDER_CLOSE','Close Purchase Order','Close purchase orders')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- GOODS RECEIPTS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('GOODS_RECEIPT_VIEW','View Goods Receipts','View goods receipts'),
('GOODS_RECEIPT_CREATE','Create Goods Receipt','Create goods receipts'),
('GOODS_RECEIPT_POST','Post Goods Receipt','Post goods receipts'),
('GOODS_RECEIPT_CANCEL','Cancel Goods Receipt','Cancel goods receipts')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- INVENTORY
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('INVENTORY_VIEW','View Inventory','View inventory'),
('INVENTORY_ADJUST','Adjust Inventory','Adjust inventory'),
('INVENTORY_TRANSFER','Transfer Inventory','Transfer inventory'),
('INVENTORY_VALUATION','Inventory Valuation','View valuation')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- STOCK COUNTS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('STOCK_COUNT_VIEW','View Stock Counts','View stock counts'),
('STOCK_COUNT_CREATE','Create Stock Count','Create stock counts'),
('STOCK_COUNT_APPROVE','Approve Stock Count','Approve stock counts')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- STOCK ADJUSTMENTS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('STOCK_ADJUSTMENT_VIEW','View Stock Adjustments','View stock adjustments'),
('STOCK_ADJUSTMENT_CREATE','Create Stock Adjustment','Create stock adjustments'),
('STOCK_ADJUSTMENT_APPROVE','Approve Stock Adjustment','Approve stock adjustments')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- STOCK TRANSFERS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('STOCK_TRANSFER_VIEW','View Stock Transfers','View stock transfers'),
('STOCK_TRANSFER_CREATE','Create Stock Transfer','Create stock transfers'),
('STOCK_TRANSFER_APPROVE','Approve Stock Transfer','Approve stock transfers'),
('STOCK_TRANSFER_RECEIVE','Receive Stock Transfer','Receive stock transfers')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- CUSTOMERS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('CUSTOMER_VIEW','View Customers','View customers'),
('CUSTOMER_CREATE','Create Customer','Create customers'),
('CUSTOMER_UPDATE','Update Customer','Update customers'),
('CUSTOMER_DELETE','Delete Customer','Delete customers')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- SALES INVOICES
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('INVOICE_VIEW','View Invoices','View invoices'),
('INVOICE_CREATE','Create Invoice','Create invoices'),
('INVOICE_UPDATE','Update Invoice','Update invoices'),
('INVOICE_APPROVE','Approve Invoice','Approve invoices'),
('INVOICE_CANCEL','Cancel Invoice','Cancel invoices')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- CUSTOMER PAYMENTS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('PAYMENT_VIEW','View Payments','View payments'),
('PAYMENT_RECEIVE','Receive Payment','Receive payments'),
('PAYMENT_REFUND','Refund Payment','Refund payments')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- REPORTS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('REPORT_VIEW','View Reports','View reports'),
('REPORT_EXPORT','Export Reports','Export reports')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- DASHBOARDS
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('DASHBOARD_VIEW','View Dashboards','View dashboards')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- AI
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('AI_PROCUREMENT','AI Procurement','Use AI procurement'),
('AI_FORECASTING','AI Forecasting','Use AI forecasting')
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- SYSTEM
--------------------------------------------------------------

INSERT INTO permissions
(code,name,description)
VALUES
('SYSTEM_SETTINGS','System Settings','Manage system settings'),
('AUDIT_LOG_VIEW','View Audit Logs','View audit logs'),
('NOTIFICATION_MANAGE','Manage Notifications','Manage notifications')
ON CONFLICT (code) DO NOTHING;