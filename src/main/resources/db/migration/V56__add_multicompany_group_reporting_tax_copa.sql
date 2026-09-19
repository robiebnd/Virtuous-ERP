CREATE TABLE IF NOT EXISTS company_codes (
 id UUID PRIMARY KEY,
 company_code VARCHAR(20) NOT NULL UNIQUE,
 company_name VARCHAR(150) NOT NULL,
 country_code VARCHAR(3) NOT NULL,
 functional_currency VARCHAR(3) NOT NULL,
 fiscal_year_variant VARCHAR(20) NOT NULL DEFAULT 'CALENDAR',
 reporting_currency VARCHAR(3),
 active BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 version BIGINT NOT NULL DEFAULT 0
);

INSERT INTO company_codes (id,company_code,company_name,country_code,functional_currency,reporting_currency)
SELECT gen_random_uuid(),'ZW01','Virtuous Zimbabwe','ZWE','USD','USD'
WHERE NOT EXISTS (SELECT 1 FROM company_codes WHERE company_code='ZW01');

ALTER TABLE consolidation_units ADD COLUMN IF NOT EXISTS company_code VARCHAR(20);
UPDATE consolidation_units SET company_code = unit_code WHERE company_code IS NULL;
UPDATE consolidation_units SET company_code = 'ZW01' WHERE company_code IS NULL;
ALTER TABLE consolidation_units ALTER COLUMN company_code SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_consolidation_unit_company ON consolidation_units(company_code);

ALTER TABLE accounting_lines ADD COLUMN IF NOT EXISTS partner_company_code VARCHAR(20);
ALTER TABLE accounting_lines ADD COLUMN IF NOT EXISTS tax_code VARCHAR(20);
ALTER TABLE accounting_lines ADD COLUMN IF NOT EXISTS tax_base NUMERIC(19,2) NOT NULL DEFAULT 0;
ALTER TABLE accounting_lines ADD COLUMN IF NOT EXISTS tax_amount NUMERIC(19,2) NOT NULL DEFAULT 0;
ALTER TABLE accounting_lines ADD COLUMN IF NOT EXISTS profitability_segment_id UUID;
CREATE INDEX IF NOT EXISTS idx_accounting_lines_company_account ON accounting_lines(company_code,gl_account_id);
CREATE INDEX IF NOT EXISTS idx_accounting_lines_partner_company ON accounting_lines(partner_company_code);
CREATE INDEX IF NOT EXISTS idx_accounting_lines_profitability_segment ON accounting_lines(profitability_segment_id);

ALTER TABLE tax_codes ADD COLUMN IF NOT EXISTS company_code VARCHAR(20);
ALTER TABLE tax_codes ADD COLUMN IF NOT EXISTS tax_type VARCHAR(30) NOT NULL DEFAULT 'VAT';
ALTER TABLE tax_codes ADD COLUMN IF NOT EXISTS jurisdiction VARCHAR(50);
ALTER TABLE tax_codes ADD COLUMN IF NOT EXISTS effective_from DATE;
ALTER TABLE tax_codes ADD COLUMN IF NOT EXISTS effective_to DATE;
ALTER TABLE tax_codes ADD COLUMN IF NOT EXISTS recoverable_percent NUMERIC(9,4) NOT NULL DEFAULT 100;
ALTER TABLE tax_codes ADD COLUMN IF NOT EXISTS tax_account_code VARCHAR(20);
UPDATE tax_codes SET company_code='ZW01' WHERE company_code IS NULL;
ALTER TABLE tax_codes ALTER COLUMN company_code SET NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS uq_tax_code_company ON tax_codes(company_code,tax_code);

CREATE TABLE IF NOT EXISTS intercompany_transactions (
 id UUID PRIMARY KEY,
 transaction_number VARCHAR(80) NOT NULL UNIQUE,
 transaction_date DATE NOT NULL,
 source_company_code VARCHAR(20) NOT NULL,
 target_company_code VARCHAR(20) NOT NULL,
 currency VARCHAR(3) NOT NULL,
 amount NUMERIC(19,2) NOT NULL,
 source_account_code VARCHAR(20) NOT NULL,
 target_account_code VARCHAR(20) NOT NULL,
 source_document_id UUID,
 target_document_id UUID,
 description VARCHAR(500),
 status VARCHAR(20) NOT NULL DEFAULT 'POSTED',
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT chk_ic_companies CHECK (source_company_code <> target_company_code),
 CONSTRAINT chk_ic_amount CHECK (amount > 0)
);

CREATE TABLE IF NOT EXISTS group_reporting_runs (
 id UUID PRIMARY KEY,
 group_id UUID NOT NULL REFERENCES consolidation_groups(id),
 fiscal_year INTEGER NOT NULL,
 period_number INTEGER NOT NULL,
 reporting_currency VARCHAR(3) NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
 total_debit NUMERIC(19,2) NOT NULL DEFAULT 0,
 total_credit NUMERIC(19,2) NOT NULL DEFAULT 0,
 translation_adjustment NUMERIC(19,2) NOT NULL DEFAULT 0,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 completed_at TIMESTAMP,
 version BIGINT NOT NULL DEFAULT 0,
 UNIQUE(group_id,fiscal_year,period_number)
);

CREATE TABLE IF NOT EXISTS group_reporting_balances (
 id UUID PRIMARY KEY,
 run_id UUID NOT NULL REFERENCES group_reporting_runs(id) ON DELETE CASCADE,
 company_code VARCHAR(20) NOT NULL,
 account_code VARCHAR(20) NOT NULL,
 account_name VARCHAR(150) NOT NULL,
 account_type VARCHAR(30) NOT NULL,
 local_debit NUMERIC(19,2) NOT NULL DEFAULT 0,
 local_credit NUMERIC(19,2) NOT NULL DEFAULT 0,
 fx_rate NUMERIC(19,8) NOT NULL DEFAULT 1,
 translated_debit NUMERIC(19,2) NOT NULL DEFAULT 0,
 translated_credit NUMERIC(19,2) NOT NULL DEFAULT 0,
 elimination_debit NUMERIC(19,2) NOT NULL DEFAULT 0,
 elimination_credit NUMERIC(19,2) NOT NULL DEFAULT 0,
 final_debit NUMERIC(19,2) NOT NULL DEFAULT 0,
 final_credit NUMERIC(19,2) NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_group_reporting_balance_run_account ON group_reporting_balances(run_id,account_code);

CREATE TABLE IF NOT EXISTS profitability_segments (
 id UUID PRIMARY KEY,
 segment_code VARCHAR(80) NOT NULL UNIQUE,
 segment_name VARCHAR(150) NOT NULL,
 company_code VARCHAR(20) NOT NULL,
 customer_id UUID,
 product_code VARCHAR(80),
 sales_channel VARCHAR(80),
 market_region VARCHAR(80),
 customer_group VARCHAR(80),
 product_group VARCHAR(80),
 active BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS profitability_postings (
 id UUID PRIMARY KEY,
 accounting_line_id UUID NOT NULL UNIQUE REFERENCES accounting_lines(id),
 segment_id UUID NOT NULL REFERENCES profitability_segments(id),
 posting_date DATE NOT NULL,
 revenue_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
 cost_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
 quantity NUMERIC(19,6) NOT NULL DEFAULT 0,
 currency VARCHAR(3) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_profitability_postings_segment_date ON profitability_postings(segment_id,posting_date);

CREATE TABLE IF NOT EXISTS tax_postings (
 id UUID PRIMARY KEY,
 accounting_document_id UUID NOT NULL REFERENCES accounting_documents(id),
 accounting_line_id UUID REFERENCES accounting_lines(id),
 company_code VARCHAR(20) NOT NULL,
 tax_code VARCHAR(20) NOT NULL,
 tax_type VARCHAR(30) NOT NULL,
 jurisdiction VARCHAR(50),
 taxable_base NUMERIC(19,2) NOT NULL,
 tax_amount NUMERIC(19,2) NOT NULL,
 input_output VARCHAR(10) NOT NULL,
 tax_account_code VARCHAR(20) NOT NULL,
 recoverable_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_tax_postings_company_date ON tax_postings(company_code,created_at);
