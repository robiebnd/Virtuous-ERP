-- SAP S/4HANA-aligned PR -> PO -> GR process fields.
-- The existing PR -> PO -> GR and GR load-PO-lines workflow is preserved.

ALTER TABLE purchase_requisitions
    ADD COLUMN IF NOT EXISTS document_type VARCHAR(20) DEFAULT 'NB',
    ADD COLUMN IF NOT EXISTS purchasing_group VARCHAR(40),
    ADD COLUMN IF NOT EXISTS plant_code VARCHAR(40),
    ADD COLUMN IF NOT EXISTS storage_location VARCHAR(40),
    ADD COLUMN IF NOT EXISTS item_category VARCHAR(30) DEFAULT 'STANDARD',
    ADD COLUMN IF NOT EXISTS account_assignment_category VARCHAR(5) DEFAULT '',
    ADD COLUMN IF NOT EXISTS requested_delivery_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS valuation_price NUMERIC(18,2),
    ADD COLUMN IF NOT EXISTS approval_level INTEGER NOT NULL DEFAULT 0;

ALTER TABLE purchase_requisition_lines
    ADD COLUMN IF NOT EXISTS item_category VARCHAR(30) DEFAULT 'STANDARD',
    ADD COLUMN IF NOT EXISTS account_assignment_category VARCHAR(5) DEFAULT '',
    ADD COLUMN IF NOT EXISTS unit_of_measure VARCHAR(20) DEFAULT 'EA',
    ADD COLUMN IF NOT EXISTS requested_delivery_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS valuation_price NUMERIC(18,2);

ALTER TABLE purchase_orders
    ADD COLUMN IF NOT EXISTS document_type VARCHAR(20) DEFAULT 'NB',
    ADD COLUMN IF NOT EXISTS purchasing_organization VARCHAR(40),
    ADD COLUMN IF NOT EXISTS purchasing_group VARCHAR(40),
    ADD COLUMN IF NOT EXISTS company_code VARCHAR(40),
    ADD COLUMN IF NOT EXISTS payment_terms VARCHAR(40),
    ADD COLUMN IF NOT EXISTS incoterms VARCHAR(40),
    ADD COLUMN IF NOT EXISTS confirmation_control VARCHAR(40),
    ADD COLUMN IF NOT EXISTS output_status VARCHAR(30) DEFAULT 'NOT_SENT';

ALTER TABLE purchase_order_lines
    ADD COLUMN IF NOT EXISTS order_unit VARCHAR(20) DEFAULT 'EA',
    ADD COLUMN IF NOT EXISTS delivery_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS storage_location VARCHAR(40),
    ADD COLUMN IF NOT EXISTS item_category VARCHAR(30) DEFAULT 'STANDARD',
    ADD COLUMN IF NOT EXISTS overdelivery_tolerance_percent NUMERIC(5,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS underdelivery_tolerance_percent NUMERIC(5,2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS goods_receipt_required BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS gr_based_invoice_verification BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS confirmation_required BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE goods_receipts
    ADD COLUMN IF NOT EXISTS movement_type VARCHAR(10) DEFAULT '101',
    ADD COLUMN IF NOT EXISTS inspection_required BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS supplier_delivery_date TIMESTAMP;

ALTER TABLE goods_receipt_lines
    ADD COLUMN IF NOT EXISTS batch_number VARCHAR(100),
    ADD COLUMN IF NOT EXISTS serial_number VARCHAR(100),
    ADD COLUMN IF NOT EXISTS storage_location VARCHAR(40),
    ADD COLUMN IF NOT EXISTS inspection_status VARCHAR(30) DEFAULT 'NOT_REQUIRED';

CREATE INDEX IF NOT EXISTS idx_pr_purchasing_group ON purchase_requisitions(purchasing_group);
CREATE INDEX IF NOT EXISTS idx_po_purchasing_group ON purchase_orders(purchasing_group);
CREATE INDEX IF NOT EXISTS idx_po_output_status ON purchase_orders(output_status);
CREATE INDEX IF NOT EXISTS idx_gr_movement_type ON goods_receipts(movement_type);
