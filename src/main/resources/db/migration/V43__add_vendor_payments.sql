-- Accounts Payable vendor payment. This is intentionally separate from customer IncomingPayment.
CREATE TABLE IF NOT EXISTS vendor_payments (
    id UUID PRIMARY KEY,
    payment_number VARCHAR(60) NOT NULL UNIQUE,
    vendor_invoice_id UUID NOT NULL REFERENCES vendor_invoices(id),
    supplier_id UUID NOT NULL REFERENCES suppliers(id),
    payment_date TIMESTAMP NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    reference_number VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    remarks VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID REFERENCES users(id),
    approved_by UUID REFERENCES users(id),
    approved_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vendor_payment_invoice ON vendor_payments(vendor_invoice_id);
CREATE INDEX IF NOT EXISTS idx_vendor_payment_supplier ON vendor_payments(supplier_id);
CREATE INDEX IF NOT EXISTS idx_vendor_payment_status ON vendor_payments(status);
