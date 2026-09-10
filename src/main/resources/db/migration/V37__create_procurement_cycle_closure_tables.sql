CREATE TABLE supplier_invoices (
    id UUID PRIMARY KEY,
    invoice_number VARCHAR(100) NOT NULL UNIQUE,
    supplier_id UUID NOT NULL REFERENCES suppliers(id),
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id),
    invoice_date TIMESTAMP NOT NULL,
    currency VARCHAR(3),
    status VARCHAR(30) NOT NULL,
    subtotal NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    amount_paid NUMERIC(18,2) NOT NULL DEFAULT 0,
    balance_due NUMERIC(18,2) NOT NULL DEFAULT 0,
    blocked_reason VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    version BIGINT
);

CREATE TABLE supplier_invoice_lines (
    id UUID PRIMARY KEY,
    supplier_invoice_id UUID NOT NULL REFERENCES supplier_invoices(id) ON DELETE CASCADE,
    purchase_order_line_id UUID NOT NULL REFERENCES purchase_order_lines(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity NUMERIC(18,2) NOT NULL,
    unit_price NUMERIC(18,2) NOT NULL,
    line_total NUMERIC(18,2) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    version BIGINT
);

CREATE TABLE supplier_payments (
    id UUID PRIMARY KEY,
    payment_number VARCHAR(100) NOT NULL UNIQUE,
    supplier_id UUID NOT NULL REFERENCES suppliers(id),
    supplier_invoice_id UUID NOT NULL REFERENCES supplier_invoices(id),
    payment_date TIMESTAMP NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    reference VARCHAR(150),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    version BIGINT
);

CREATE TABLE vendor_evaluations (
    id UUID PRIMARY KEY,
    supplier_id UUID NOT NULL REFERENCES suppliers(id),
    purchase_order_id UUID REFERENCES purchase_orders(id),
    price_score NUMERIC(5,2) NOT NULL,
    quality_score NUMERIC(5,2) NOT NULL,
    delivery_score NUMERIC(5,2) NOT NULL,
    service_score NUMERIC(5,2) NOT NULL,
    overall_score NUMERIC(5,2) NOT NULL,
    evaluation_date TIMESTAMP NOT NULL,
    remarks VARCHAR(1000),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    version BIGINT
);

CREATE INDEX idx_supplier_invoice_po ON supplier_invoices(purchase_order_id);
CREATE INDEX idx_supplier_invoice_supplier ON supplier_invoices(supplier_id);
CREATE INDEX idx_supplier_invoice_line_po_line ON supplier_invoice_lines(purchase_order_line_id);
CREATE INDEX idx_supplier_payment_invoice ON supplier_payments(supplier_invoice_id);
CREATE INDEX idx_vendor_evaluation_supplier ON vendor_evaluations(supplier_id);
