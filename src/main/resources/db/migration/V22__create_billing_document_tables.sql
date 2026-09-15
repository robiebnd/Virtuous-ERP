CREATE TABLE IF NOT EXISTS billing_documents (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    remarks VARCHAR(3000),
    billing_number VARCHAR(40) NOT NULL UNIQUE,
    outbound_delivery_id UUID NOT NULL UNIQUE,
    customer_code VARCHAR(40) NOT NULL,
    billing_type VARCHAR(20) NOT NULL DEFAULT 'F2',
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL,
    billing_date TIMESTAMP NOT NULL,
    total_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_billing_delivery
        FOREIGN KEY (outbound_delivery_id) REFERENCES outbound_deliveries(id)
);

CREATE TABLE IF NOT EXISTS billing_document_items (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    billing_document_id UUID NOT NULL,
    item_number INTEGER NOT NULL,
    material_code VARCHAR(40) NOT NULL,
    quantity NUMERIC(19,3) NOT NULL,
    unit_price NUMERIC(19,2) NOT NULL,
    net_value NUMERIC(19,2) NOT NULL,
    CONSTRAINT fk_billing_items_document
        FOREIGN KEY (billing_document_id) REFERENCES billing_documents(id) ON DELETE CASCADE,
    CONSTRAINT uq_billing_item_number
        UNIQUE (billing_document_id, item_number)
);

CREATE INDEX IF NOT EXISTS idx_billing_documents_customer_code
    ON billing_documents(customer_code);

CREATE INDEX IF NOT EXISTS idx_billing_documents_status
    ON billing_documents(status);
