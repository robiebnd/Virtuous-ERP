ALTER TABLE intercompany_transactions ADD COLUMN IF NOT EXISTS source_debit_account_code VARCHAR(20);
ALTER TABLE intercompany_transactions ADD COLUMN IF NOT EXISTS source_credit_account_code VARCHAR(20);
ALTER TABLE intercompany_transactions ADD COLUMN IF NOT EXISTS target_debit_account_code VARCHAR(20);
ALTER TABLE intercompany_transactions ADD COLUMN IF NOT EXISTS target_credit_account_code VARCHAR(20);

ALTER TABLE tax_codes DROP CONSTRAINT IF EXISTS tax_codes_tax_code_key;
CREATE UNIQUE INDEX IF NOT EXISTS uq_tax_code_company_v57 ON tax_codes(company_code,tax_code);

ALTER TABLE accounting_lines ADD CONSTRAINT chk_accounting_line_tax_nonnegative CHECK (tax_base >= 0 AND tax_amount >= 0);
ALTER TABLE tax_postings ADD CONSTRAINT chk_tax_posting_amounts CHECK (taxable_base >= 0 AND tax_amount >= 0 AND recoverable_amount >= 0);
