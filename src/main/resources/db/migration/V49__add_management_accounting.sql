ALTER TABLE accounting_lines
    ADD COLUMN IF NOT EXISTS internal_order_code VARCHAR(40),
    ADD COLUMN IF NOT EXISTS wbs_element VARCHAR(60);

CREATE INDEX IF NOT EXISTS idx_accounting_lines_cost_center ON accounting_lines(cost_center);
CREATE INDEX IF NOT EXISTS idx_accounting_lines_internal_order ON accounting_lines(internal_order_code);
CREATE INDEX IF NOT EXISTS idx_accounting_lines_wbs ON accounting_lines(wbs_element);

CREATE TABLE IF NOT EXISTS cost_center_budgets (
    id UUID PRIMARY KEY,
    cost_center_code VARCHAR(30) NOT NULL,
    fiscal_year INTEGER NOT NULL,
    budget_amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    description VARCHAR(250),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_cost_center_budget UNIQUE(cost_center_code, fiscal_year),
    CONSTRAINT chk_cost_center_budget_amount CHECK (budget_amount >= 0),
    CONSTRAINT chk_cost_center_budget_year CHECK (fiscal_year BETWEEN 2000 AND 2100)
);

CREATE INDEX IF NOT EXISTS idx_cc_budget_year ON cost_center_budgets(fiscal_year);

CREATE TABLE IF NOT EXISTS internal_orders (
    id UUID PRIMARY KEY,
    order_code VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    cost_center_code VARCHAR(30),
    responsible_person VARCHAR(150),
    budget_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT chk_internal_order_budget CHECK (budget_amount >= 0),
    CONSTRAINT chk_internal_order_status CHECK (status IN ('OPEN','CLOSED','CANCELLED'))
);

CREATE INDEX IF NOT EXISTS idx_internal_orders_cost_center ON internal_orders(cost_center_code);
CREATE INDEX IF NOT EXISTS idx_internal_orders_status ON internal_orders(status);
