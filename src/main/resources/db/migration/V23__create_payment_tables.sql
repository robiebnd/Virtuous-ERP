CREATE TABLE IF NOT EXISTS incoming_payments (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    remarks VARCHAR(3000),
    payment_number VARCHAR(40) NOT NULL UNIQUE,
    customer_code VARCHAR(40) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    reference VARCHAR(100),
    status VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS payment_allocations (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    payment_id UUID NOT NULL,
    billing_document_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    CONSTRAINT fk_payment_allocation_payment
        FOREIGN KEY (payment_id) REFERENCES incoming_payments(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_allocation_billing
        FOREIGN KEY (billing_document_id) REFERENCES billing_documents(id),
    CONSTRAINT chk_payment_allocation_amount
        CHECK (amount > 0)
);

CREATE INDEX IF NOT EXISTS idx_incoming_payments_customer_code
    ON incoming_payments(customer_code);

CREATE INDEX IF NOT EXISTS idx_incoming_payments_status
    ON incoming_payments(status);

CREATE INDEX IF NOT EXISTS idx_payment_allocations_billing_document
    ON payment_allocations(billing_document_id);
