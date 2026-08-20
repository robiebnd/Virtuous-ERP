-- ==========================================================
-- V32 - SOURCE OF SUPPLY TRACEABILITY
-- Link Purchase Requisition lines to their selected source of
-- supply / purchasing info record, and carry that trace to PO lines.
-- ==========================================================

ALTER TABLE purchase_requisition_lines
    ADD COLUMN IF NOT EXISTS source_supplier_id UUID,
    ADD COLUMN IF NOT EXISTS purchasing_info_record_id UUID;

ALTER TABLE purchase_requisition_lines
    ADD CONSTRAINT fk_pr_line_source_supplier
        FOREIGN KEY (source_supplier_id)
        REFERENCES suppliers(id);

ALTER TABLE purchase_requisition_lines
    ADD CONSTRAINT fk_pr_line_purchasing_info
        FOREIGN KEY (purchasing_info_record_id)
        REFERENCES purchasing_info_records(id);

CREATE INDEX IF NOT EXISTS idx_pr_line_source_supplier
    ON purchase_requisition_lines(source_supplier_id);

CREATE INDEX IF NOT EXISTS idx_pr_line_purchasing_info
    ON purchase_requisition_lines(purchasing_info_record_id);

ALTER TABLE purchase_order_lines
    ADD COLUMN IF NOT EXISTS purchase_requisition_line_id UUID;

ALTER TABLE purchase_order_lines
    ADD CONSTRAINT fk_po_line_purchase_requisition_line
        FOREIGN KEY (purchase_requisition_line_id)
        REFERENCES purchase_requisition_lines(id);

CREATE INDEX IF NOT EXISTS idx_po_line_purchase_requisition_line
    ON purchase_order_lines(purchase_requisition_line_id);
