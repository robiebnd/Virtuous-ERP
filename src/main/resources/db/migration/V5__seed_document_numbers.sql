-- ==========================================================
-- V5__seed_document_numbers.sql
-- DOCUMENT NUMBER SEQUENCES
-- ==========================================================

INSERT INTO document_sequences
(
    id,
    document_type,
    prefix,
    suffix,
    current_number,
    padding,
    financial_year,
    created_at,
    updated_at,
    active,
    version
)
VALUES

--------------------------------------------------------------
-- PROCUREMENT
--------------------------------------------------------------

(
    gen_random_uuid(),
    'PURCHASE_REQUISITION',
    'PR-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

(
    gen_random_uuid(),
    'PURCHASE_ORDER',
    'PO-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

(
    gen_random_uuid(),
    'GOODS_RECEIPT',
    'GRN-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
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
    'STOCK_TRANSFER',
    'ST-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

(
    gen_random_uuid(),
    'STOCK_COUNT',
    'SC-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

(
    gen_random_uuid(),
    'STOCK_ADJUSTMENT',
    'SA-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
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
    'INVOICE',
    'INV-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

(
    gen_random_uuid(),
    'CUSTOMER_PAYMENT',
    'PAY-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
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
    'JOURNAL_ENTRY',
    'JV-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- WAREHOUSE
--------------------------------------------------------------

(
    gen_random_uuid(),
    'INVENTORY_TRANSACTION',
    'IT-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
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
    'AUDIT_LOG',
    'AUD-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

(
    gen_random_uuid(),
    'IMPORT_JOB',
    'IMP-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

(
    gen_random_uuid(),
    'EXPORT_JOB',
    'EXP-',
    '',
    1,
    6,
    EXTRACT(YEAR FROM CURRENT_DATE),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
)

ON CONFLICT (document_type, financial_year)
DO NOTHING;