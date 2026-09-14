CREATE TABLE IF NOT EXISTS vendor_payments (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    version BIGINT,
    payment_number VARCHAR(60) NOT NULL UNIQUE,
    supplier_id UUID NOT NULL REFERENCES suppliers(id),
    vendor_invoice_id UUID NOT NULL REFERENCES vendor_invoices(id),
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_date TIMESTAMP NOT NULL,
    reference VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    processed_by UUID REFERENCES users(id),
    remarks VARCHAR(1000)
);

CREATE INDEX IF NOT EXISTS idx_vendor_payments_invoice ON vendor_payments(vendor_invoice_id);
CREATE INDEX IF NOT EXISTS idx_vendor_payments_supplier ON vendor_payments(supplier_id);

CREATE TABLE IF NOT EXISTS vendor_evaluations (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    version BIGINT,
    supplier_id UUID NOT NULL REFERENCES suppliers(id),
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id),
    purchase_order_number VARCHAR(50) NOT NULL,
    price_score NUMERIC(5,2) NOT NULL,
    quality_score NUMERIC(5,2) NOT NULL,
    delivery_score NUMERIC(5,2) NOT NULL,
    service_score NUMERIC(5,2) NOT NULL,
    overall_score NUMERIC(5,2) NOT NULL,
    evaluation_date TIMESTAMP NOT NULL,
    remarks VARCHAR(1000),
    CONSTRAINT uq_vendor_evaluation_po UNIQUE(purchase_order_id),
    CONSTRAINT chk_vendor_eval_scores CHECK (
        price_score BETWEEN 0 AND 100 AND
        quality_score BETWEEN 0 AND 100 AND
        delivery_score BETWEEN 0 AND 100 AND
        service_score BETWEEN 0 AND 100 AND
        overall_score BETWEEN 0 AND 100
    )
);

CREATE INDEX IF NOT EXISTS idx_vendor_evaluations_supplier ON vendor_evaluations(supplier_id);
