-- ==========================================================
-- V6__seed_system_settings.sql
-- ERP SYSTEM SETTINGS
-- ==========================================================

INSERT INTO system_settings
(
    id,
    setting_key,
    setting_value,
    description,
    created_at,
    updated_at,
    active,
    version
)
VALUES

--------------------------------------------------------------
-- COMPANY INFORMATION
--------------------------------------------------------------

(
gen_random_uuid(),
'COMPANY_NAME',
'Digipals ERP',
'Company Name',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'COMPANY_ADDRESS',
'Harare, Zimbabwe',
'Company Address',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'COMPANY_PHONE',
'+263000000000',
'Company Phone',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'COMPANY_EMAIL',
'info@company.com',
'Company Email',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'COMPANY_WEBSITE',
'https://www.company.com',
'Company Website',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'VAT_NUMBER',
'',
'Company VAT Number',
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
'DEFAULT_WAREHOUSE',
'',
'Default Warehouse',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'ALLOW_NEGATIVE_STOCK',
'false',
'Allow Negative Stock',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'AUTO_RESERVE_STOCK',
'true',
'Automatically Reserve Stock',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'DEFAULT_REORDER_LEVEL',
'0',
'Default Reorder Level',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'INVENTORY_VALUATION_METHOD',
'AVERAGE_COST',
'Inventory Valuation Method',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- PROCUREMENT
--------------------------------------------------------------

(
gen_random_uuid(),
'AUTO_GENERATE_PO',
'true',
'Automatically Generate Purchase Orders',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'PURCHASE_APPROVAL_REQUIRED',
'true',
'Purchase Approval Required',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'DEFAULT_SUPPLIER_PAYMENT_DAYS',
'30',
'Supplier Payment Terms',
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
'DEFAULT_CUSTOMER_PAYMENT_DAYS',
'30',
'Customer Payment Terms',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'AUTO_POST_INVOICE',
'false',
'Automatically Post Invoices',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- FINANCE
--------------------------------------------------------------

(
gen_random_uuid(),
'BASE_CURRENCY',
'USD',
'Base Currency',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'FINANCIAL_YEAR_START_MONTH',
'1',
'Financial Year Start Month',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'DECIMAL_PLACES',
'2',
'Currency Decimal Places',
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
'PASSWORD_EXPIRY_DAYS',
'90',
'Password Expiry',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'MAX_LOGIN_ATTEMPTS',
'5',
'Maximum Login Attempts',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'SESSION_TIMEOUT_MINUTES',
'30',
'Session Timeout',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- EMAIL
--------------------------------------------------------------

(
gen_random_uuid(),
'SMTP_ENABLED',
'false',
'Enable SMTP',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'SMTP_HOST',
'',
'SMTP Host',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'SMTP_PORT',
'587',
'SMTP Port',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'SMTP_USERNAME',
'',
'SMTP Username',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'SMTP_PASSWORD',
'',
'SMTP Password',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'SMTP_TLS',
'true',
'Use TLS',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- PDF
--------------------------------------------------------------

(
gen_random_uuid(),
'PDF_COMPANY_LOGO',
'',
'Company Logo',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'PDF_FOOTER',
'Generated by Digipals ERP',
'PDF Footer',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'PDF_SIGNATURE',
'',
'Digital Signature',
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
'AI_ENABLED',
'false',
'Enable Artificial Intelligence',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'AI_PROVIDER',
'OPENAI',
'AI Provider',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'AI_MODEL',
'gpt-5.5',
'AI Model',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'AI_FORECAST_MONTHS',
'3',
'Demand Forecast Horizon',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'OPENAI_API_KEY',
'',
'OpenAI API Key',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

--------------------------------------------------------------
-- NOTIFICATIONS
--------------------------------------------------------------

(
gen_random_uuid(),
'EMAIL_NOTIFICATIONS',
'true',
'Enable Email Notifications',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'SMS_NOTIFICATIONS',
'false',
'Enable SMS Notifications',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'PUSH_NOTIFICATIONS',
'true',
'Enable Push Notifications',
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
'REPORT_TIMEZONE',
'Africa/Harare',
'Reporting Timezone',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'DEFAULT_REPORT_FORMAT',
'PDF',
'Default Report Format',
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
'SYSTEM_LANGUAGE',
'en',
'Default Language',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'SYSTEM_TIMEZONE',
'Africa/Harare',
'System Timezone',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
),

(
gen_random_uuid(),
'SYSTEM_VERSION',
'1.0.0',
'ERP Version',
CURRENT_TIMESTAMP,
CURRENT_TIMESTAMP,
TRUE,
0
)

ON CONFLICT (setting_key)
DO NOTHING;