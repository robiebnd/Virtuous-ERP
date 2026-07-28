-- ==========================================================
-- V9__seed_notification_templates.sql
-- NOTIFICATION TEMPLATES
-- ==========================================================

INSERT INTO notification_templates
(
    id,
    template_code,
    template_name,
    subject,
    message,
    notification_type,
    enabled,
    created_at,
    updated_at,
    active,
    version
)
VALUES

--------------------------------------------------------------
-- USER MANAGEMENT
--------------------------------------------------------------

(
gen_random_uuid(),
'WELCOME_USER',
'Welcome User',
'Welcome to Digipals ERP',
'Hello {{username}}, your account has been created successfully.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'PASSWORD_RESET',
'Password Reset',
'Password Reset Request',
'Click the password reset link to change your password.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- PURCHASE REQUISITIONS
--------------------------------------------------------------

(
gen_random_uuid(),
'PURCHASE_REQUISITION_SUBMITTED',
'Purchase Requisition Submitted',
'Purchase Requisition Submitted',
'Purchase Requisition {{documentNumber}} has been submitted for approval.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'PURCHASE_REQUISITION_APPROVED',
'Purchase Requisition Approved',
'Purchase Requisition Approved',
'Purchase Requisition {{documentNumber}} has been approved.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'PURCHASE_REQUISITION_REJECTED',
'Purchase Requisition Rejected',
'Purchase Requisition Rejected',
'Purchase Requisition {{documentNumber}} has been rejected. Reason: {{remarks}}',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- PURCHASE ORDERS
--------------------------------------------------------------

(
gen_random_uuid(),
'PURCHASE_ORDER_CREATED',
'Purchase Order Created',
'Purchase Order Created',
'Purchase Order {{documentNumber}} has been created.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'PURCHASE_ORDER_APPROVED',
'Purchase Order Approved',
'Purchase Order Approved',
'Purchase Order {{documentNumber}} has been approved.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- GOODS RECEIPTS
--------------------------------------------------------------

(
gen_random_uuid(),
'GOODS_RECEIPT_POSTED',
'Goods Receipt Posted',
'Goods Receipt Posted',
'Goods Receipt {{documentNumber}} has been successfully posted.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- INVENTORY
--------------------------------------------------------------

(
gen_random_uuid(),
'LOW_STOCK',
'Low Stock Alert',
'Low Stock Alert',
'Product {{productName}} has fallen below the reorder level.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'OUT_OF_STOCK',
'Out Of Stock',
'Out Of Stock',
'Product {{productName}} is now out of stock.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'STOCK_TRANSFER_RECEIVED',
'Stock Transfer Received',
'Stock Transfer Received',
'Stock Transfer {{documentNumber}} has been received.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- SALES
--------------------------------------------------------------

(
gen_random_uuid(),
'INVOICE_CREATED',
'Invoice Created',
'Invoice Created',
'Invoice {{documentNumber}} has been created.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'PAYMENT_RECEIVED',
'Payment Received',
'Payment Received',
'Payment of {{amount}} has been received for Invoice {{documentNumber}}.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- SECURITY
--------------------------------------------------------------

(
gen_random_uuid(),
'LOGIN_ALERT',
'Login Alert',
'New Login Detected',
'Your account has logged in from {{ipAddress}}.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'ACCOUNT_LOCKED',
'Account Locked',
'Account Locked',
'Your account has been locked after multiple unsuccessful login attempts. Please contact your system administrator.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- SYSTEM
--------------------------------------------------------------

(
gen_random_uuid(),
'SYSTEM_ERROR',
'System Error',
'System Error',
'A system error has occurred. Please review the application logs for more information.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'BACKUP_COMPLETED',
'Database Backup Completed',
'Database Backup Completed',
'The scheduled database backup completed successfully.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'BACKUP_FAILED',
'Database Backup Failed',
'Database Backup Failed',
'The scheduled database backup has failed. Immediate attention is required.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- AI
--------------------------------------------------------------

(
gen_random_uuid(),
'AI_REORDER_RECOMMENDATION',
'AI Reorder Recommendation',
'Inventory Replenishment Recommendation',
'AI recommends reordering {{productName}}. Suggested quantity: {{quantity}}. Confidence: {{confidence}}%.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'AI_FORECAST_READY',
'Demand Forecast Ready',
'Monthly Demand Forecast',
'The latest demand forecast has been generated successfully.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- REPORTS
--------------------------------------------------------------

(
gen_random_uuid(),
'REPORT_GENERATED',
'Report Generated',
'Requested Report Ready',
'Your requested report {{reportName}} has been generated successfully.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'REPORT_SCHEDULED',
'Scheduled Report',
'Scheduled Report Generated',
'Your scheduled report {{reportName}} has been generated and is ready for download.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- IMPORT / EXPORT
--------------------------------------------------------------

(
gen_random_uuid(),
'IMPORT_COMPLETED',
'Import Completed',
'Import Completed Successfully',
'Import job {{jobName}} completed successfully. Imported {{successfulRecords}} records.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'IMPORT_FAILED',
'Import Failed',
'Import Failed',
'Import job {{jobName}} failed. Please review the error log.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'EXPORT_COMPLETED',
'Export Completed',
'Export Completed Successfully',
'Export job {{jobName}} has completed successfully.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- APPROVALS
--------------------------------------------------------------

(
gen_random_uuid(),
'APPROVAL_REQUIRED',
'Approval Required',
'Approval Required',
'Document {{documentNumber}} requires your approval.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'APPROVAL_COMPLETED',
'Approval Completed',
'Approval Completed',
'Document {{documentNumber}} has been approved successfully.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- DOCUMENTS
--------------------------------------------------------------

(
gen_random_uuid(),
'PDF_GENERATED',
'PDF Generated',
'Document Generated',
'The PDF for document {{documentNumber}} has been generated successfully.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'DOCUMENT_CANCELLED',
'Document Cancelled',
'Document Cancelled',
'Document {{documentNumber}} has been cancelled.',
'EMAIL',
TRUE,
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
)

ON CONFLICT (template_code)
DO NOTHING;