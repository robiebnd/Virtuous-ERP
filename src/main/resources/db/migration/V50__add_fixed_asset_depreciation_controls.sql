ALTER TABLE fixed_assets
    ADD COLUMN IF NOT EXISTS last_depreciation_date DATE;

CREATE TABLE IF NOT EXISTS asset_depreciation_runs (
    id UUID PRIMARY KEY,
    asset_id UUID NOT NULL REFERENCES fixed_assets(id),
    period_start DATE NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    accounting_document_id UUID NOT NULL REFERENCES accounting_documents(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(asset_id, period_start),
    CONSTRAINT chk_depreciation_run_amount CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_depreciation_runs_asset ON asset_depreciation_runs(asset_id);

INSERT INTO gl_accounts (id, account_code, account_name, account_type, control_account)
VALUES ('10000000-0000-0000-0000-000000000009','165000','Accumulated Depreciation','ASSET',FALSE)
ON CONFLICT (account_code) DO NOTHING;
