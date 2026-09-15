CREATE TABLE IF NOT EXISTS dunning_cases (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    remarks VARCHAR(3000),
    dunning_number VARCHAR(40) NOT NULL UNIQUE,
    billing_document_id UUID NOT NULL,
    customer_code VARCHAR(40) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    outstanding_amount NUMERIC(19,2) NOT NULL,
    due_date TIMESTAMP NOT NULL,
    dunning_date TIMESTAMP NOT NULL,
    dunning_level INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    message VARCHAR(3000),
    CONSTRAINT fk_dunning_billing_document
        FOREIGN KEY (billing_document_id) REFERENCES billing_documents(id)
);

CREATE INDEX IF NOT EXISTS idx_dunning_cases_customer_code
    ON dunning_cases(customer_code);

CREATE INDEX IF NOT EXISTS idx_dunning_cases_status
    ON dunning_cases(status);

CREATE INDEX IF NOT EXISTS idx_dunning_cases_due_date
    ON dunning_cases(due_date);
