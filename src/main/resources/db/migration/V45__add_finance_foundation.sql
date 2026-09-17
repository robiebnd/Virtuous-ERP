CREATE TABLE IF NOT EXISTS gl_accounts (
    id UUID PRIMARY KEY,
    account_code VARCHAR(20) NOT NULL UNIQUE,
    account_name VARCHAR(150) NOT NULL,
    account_type VARCHAR(30) NOT NULL,
    control_account BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE gl_accounts
    ADD COLUMN IF NOT EXISTS control_account BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE gl_accounts
    ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE gl_accounts
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE gl_accounts
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE gl_accounts
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

CREATE TABLE IF NOT EXISTS accounting_documents (
    id UUID PRIMARY KEY,
    document_number VARCHAR(60) NOT NULL UNIQUE,
    document_type VARCHAR(40) NOT NULL,
    document_date TIMESTAMP NOT NULL,
    posting_date TIMESTAMP NOT NULL,
    company_code VARCHAR(20) NOT NULL DEFAULT 'ZW01',
    currency VARCHAR(3) NOT NULL,
    reference_type VARCHAR(40),
    reference_id UUID,
    reference_number VARCHAR(100),
    description VARCHAR(500),
    total_debit NUMERIC(19,2) NOT NULL DEFAULT 0,
    total_credit NUMERIC(19,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'POSTED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS accounting_lines (
    id UUID PRIMARY KEY,
    accounting_document_id UUID NOT NULL REFERENCES accounting_documents(id),
    gl_account_id UUID NOT NULL REFERENCES gl_accounts(id),
    line_number INTEGER NOT NULL,
    debit NUMERIC(19,2) NOT NULL DEFAULT 0,
    credit NUMERIC(19,2) NOT NULL DEFAULT 0,
    company_code VARCHAR(20) NOT NULL DEFAULT 'ZW01',
    cost_center VARCHAR(40),
    profit_center VARCHAR(40),
    functional_area VARCHAR(40),
    segment VARCHAR(40),
    line_text VARCHAR(500),
    UNIQUE(accounting_document_id, line_number)
);

CREATE INDEX IF NOT EXISTS idx_accounting_documents_posting_date ON accounting_documents(posting_date);
CREATE INDEX IF NOT EXISTS idx_accounting_documents_reference ON accounting_documents(reference_type, reference_id);
CREATE INDEX IF NOT EXISTS idx_accounting_documents_status ON accounting_documents(status);
CREATE INDEX IF NOT EXISTS idx_accounting_lines_account ON accounting_lines(gl_account_id);
CREATE INDEX IF NOT EXISTS idx_accounting_lines_company ON accounting_lines(company_code);

INSERT INTO gl_accounts (id, account_code, account_name, account_type, control_account)
VALUES
 ('10000000-0000-0000-0000-000000000001','110000','Inventory','ASSET',FALSE),
 ('10000000-0000-0000-0000-000000000002','120000','Accounts Receivable','ASSET',TRUE),
 ('10000000-0000-0000-0000-000000000003','100000','Bank','ASSET',TRUE),
 ('10000000-0000-0000-0000-000000000004','210000','Goods Received / Invoice Received','LIABILITY',FALSE),
 ('10000000-0000-0000-0000-000000000005','200000','Accounts Payable','LIABILITY',TRUE),
 ('10000000-0000-0000-0000-000000000006','400000','Sales Revenue','REVENUE',FALSE),
 ('10000000-0000-0000-0000-000000000007','500000','Cost of Goods Sold','EXPENSE',FALSE),
 ('10000000-0000-0000-0000-000000000008','610000','Operating Expense','EXPENSE',FALSE)
ON CONFLICT (account_code) DO NOTHING;
