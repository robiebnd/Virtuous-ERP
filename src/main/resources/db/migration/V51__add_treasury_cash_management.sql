CREATE TABLE IF NOT EXISTS bank_transactions (
    id UUID PRIMARY KEY,
    bank_account_id UUID NOT NULL REFERENCES bank_accounts(id),
    transaction_number VARCHAR(80) NOT NULL,
    transaction_date DATE NOT NULL,
    value_date DATE,
    amount NUMERIC(19,2) NOT NULL,
    direction VARCHAR(10) NOT NULL,
    reference VARCHAR(150),
    description VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'UNRECONCILED',
    accounting_document_id UUID REFERENCES accounting_documents(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_bank_transaction UNIQUE(bank_account_id, transaction_number),
    CONSTRAINT chk_bank_transaction_amount CHECK (amount > 0),
    CONSTRAINT chk_bank_transaction_direction CHECK (direction IN ('IN','OUT')),
    CONSTRAINT chk_bank_transaction_status CHECK (status IN ('UNRECONCILED','RECONCILED','EXCLUDED'))
);

CREATE INDEX IF NOT EXISTS idx_bank_transactions_account_date ON bank_transactions(bank_account_id, transaction_date);
CREATE INDEX IF NOT EXISTS idx_bank_transactions_status ON bank_transactions(status);

CREATE TABLE IF NOT EXISTS liquidity_forecasts (
    id UUID PRIMARY KEY,
    forecast_date DATE NOT NULL,
    category VARCHAR(80) NOT NULL,
    description VARCHAR(250),
    expected_inflow NUMERIC(19,2) NOT NULL DEFAULT 0,
    expected_outflow NUMERIC(19,2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_liquidity_inflow CHECK (expected_inflow >= 0),
    CONSTRAINT chk_liquidity_outflow CHECK (expected_outflow >= 0),
    CONSTRAINT chk_liquidity_status CHECK (status IN ('OPEN','CONFIRMED','CANCELLED'))
);

CREATE INDEX IF NOT EXISTS idx_liquidity_forecast_date ON liquidity_forecasts(forecast_date);
