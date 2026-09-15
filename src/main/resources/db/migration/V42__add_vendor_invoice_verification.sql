-- Vendor AP invoice verification. This is separate from outbound customer billing.
CREATE TABLE IF NOT EXISTS vendor_invoices (
    id UUID PRIMARY KEY,
    invoice_number VARCHAR(60) NOT NULL UNIQUE,
    supplier_id UUID NOT NULL REFERENCES suppliers(id),
    purchase_order_id UUID NOT NULL REFERENCES purchase_orders(id),
    goods_receipt_id UUID NOT NULL REFERENCES goods_receipts(id),
    supplier_invoice_number VARCHAR(100) NOT NULL,
    invoice_date TIMESTAMP NOT NULL,
    currency VARCHAR(3) NOT NULL,
    subtotal NUMERIC(19,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(19,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    match_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    block_reason VARCHAR(1000),
    posted_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by UUID REFERENCES users(id),
    approved_by UUID REFERENCES users(id),
    approved_at TIMESTAMP,
    CONSTRAINT uq_vendor_invoice_supplier_number UNIQUE (supplier_id, supplier_invoice_number)
);

CREATE TABLE IF NOT EXISTS vendor_invoice_lines (
    id UUID PRIMARY KEY,
    vendor_invoice_id UUID NOT NULL REFERENCES vendor_invoices(id) ON DELETE CASCADE,
    purchase_order_line_id UUID NOT NULL REFERENCES purchase_order_lines(id),
    goods_receipt_line_id UUID REFERENCES goods_receipt_lines(id),
    product_id UUID NOT NULL REFERENCES products(id),
    invoiced_quantity NUMERIC(18,2) NOT NULL,
    invoice_unit_price NUMERIC(18,2) NOT NULL,
    line_total NUMERIC(19,2) NOT NULL,
    quantity_variance NUMERIC(18,2) NOT NULL DEFAULT 0,
    price_variance NUMERIC(18,2) NOT NULL DEFAULT 0,
    match_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    block_reason VARCHAR(1000),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_vendor_invoice_po ON vendor_invoices(purchase_order_id);
CREATE INDEX IF NOT EXISTS idx_vendor_invoice_gr ON vendor_invoices(goods_receipt_id);
CREATE INDEX IF NOT EXISTS idx_vendor_invoice_supplier ON vendor_invoices(supplier_id);
CREATE INDEX IF NOT EXISTS idx_vendor_invoice_status ON vendor_invoices(status, match_status);
CREATE INDEX IF NOT EXISTS idx_vendor_invoice_line_po ON vendor_invoice_lines(purchase_order_line_id);
