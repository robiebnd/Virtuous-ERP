-- ==========================================================
-- V8__seed_gl_accounts.sql
-- CHART OF ACCOUNTS
-- PART 1 - ASSETS
-- ==========================================================

INSERT INTO gl_accounts
(
    id,
    account_code,
    account_name,
    account_type,
    parent_account_id,
    posting_allowed,
    created_at,
    updated_at,
    active,
    version
)
VALUES

--------------------------------------------------------------
-- CURRENT ASSETS
--------------------------------------------------------------

(gen_random_uuid(),'1000','Current Assets','ASSET',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1100','Cash on Hand','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1110','Cash at Bank','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1120','Petty Cash','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1200','Accounts Receivable','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1210','Trade Debtors','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1220','Other Receivables','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1300','Inventory','ASSET',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1310','Raw Materials','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1320','Finished Goods','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1330','Packaging Materials','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1340','Work In Progress','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1350','Goods In Transit','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1400','Prepaid Expenses','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1500','VAT Receivable','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1510','Input VAT','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

--------------------------------------------------------------
-- NON CURRENT ASSETS
--------------------------------------------------------------

(gen_random_uuid(),'1600','Property Plant & Equipment','ASSET',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1610','Land','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1620','Buildings','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1630','Warehouse Equipment','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1640','Motor Vehicles','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1650','Office Equipment','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1660','Computer Equipment','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1700','Accumulated Depreciation','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1800','Intangible Assets','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1810','Software','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'1820','Licences','ASSET',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0)

ON CONFLICT (account_code)
DO NOTHING;

INSERT INTO gl_accounts
(
id,
account_code,
account_name,
account_type,
parent_account_id,
posting_allowed,
created_at,
updated_at,
active,
version
)
VALUES

(gen_random_uuid(),'2000','Current Liabilities','LIABILITY',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'2100','Accounts Payable','LIABILITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'2110','Trade Creditors','LIABILITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'2120','Accrued Expenses','LIABILITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'2130','Supplier Deposits','LIABILITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'2200','VAT Payable','LIABILITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'2210','Output VAT','LIABILITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'2300','Payroll Liabilities','LIABILITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'2400','Income Tax Payable','LIABILITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'2500','Long Term Loans','LIABILITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0)

ON CONFLICT (account_code)
DO NOTHING;

INSERT INTO gl_accounts
(
id,
account_code,
account_name,
account_type,
parent_account_id,
posting_allowed,
created_at,
updated_at,
active,
version
)
VALUES

(gen_random_uuid(),'3000','Owner Equity','EQUITY',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'3100','Share Capital','EQUITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'3200','Retained Earnings','EQUITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'3300','Current Year Earnings','EQUITY',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0)

ON CONFLICT (account_code)
DO NOTHING;

INSERT INTO gl_accounts
(
id,
account_code,
account_name,
account_type,
parent_account_id,
posting_allowed,
created_at,
updated_at,
active,
version
)
VALUES

(gen_random_uuid(),'4000','Revenue','REVENUE',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'4100','Product Sales','REVENUE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'4200','Service Income','REVENUE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'4300','Other Operating Income','REVENUE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0)

ON CONFLICT (account_code)
DO NOTHING;

INSERT INTO gl_accounts
(
id,
account_code,
account_name,
account_type,
parent_account_id,
posting_allowed,
created_at,
updated_at,
active,
version
)
VALUES

(gen_random_uuid(),'5000','Cost of Sales','EXPENSE',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'5100','Cost of Goods Sold','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'5200','Purchase Price Variance','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'5300','Inventory Write Off','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0)

ON CONFLICT (account_code)
DO NOTHING;

INSERT INTO gl_accounts
(
id,
account_code,
account_name,
account_type,
parent_account_id,
posting_allowed,
created_at,
updated_at,
active,
version
)
VALUES

(gen_random_uuid(),'6000','Operating Expenses','EXPENSE',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6100','Salaries and Wages','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6110','Employee Benefits','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6200','Rent Expense','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6300','Utilities','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6400','Fuel Expense','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6500','Vehicle Maintenance','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6600','Office Expenses','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6700','IT Expenses','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6800','Depreciation Expense','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'6900','Marketing Expense','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0)

ON CONFLICT (account_code)
DO NOTHING;

INSERT INTO gl_accounts
(
id,
account_code,
account_name,
account_type,
parent_account_id,
posting_allowed,
created_at,
updated_at,
active,
version
)
VALUES

(gen_random_uuid(),'8000','Other Income','REVENUE',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'8100','Interest Income','REVENUE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'8200','Foreign Exchange Gain','REVENUE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'9000','Other Expenses','EXPENSE',NULL,FALSE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'9100','Bank Charges','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0),

(gen_random_uuid(),'9200','Foreign Exchange Loss','EXPENSE',NULL,TRUE,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0)

ON CONFLICT (account_code)
DO NOTHING;

