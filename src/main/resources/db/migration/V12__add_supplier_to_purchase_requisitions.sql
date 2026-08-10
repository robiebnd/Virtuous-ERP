-- ============================================================
-- V12 - Add Supplier to Purchase Requisitions
-- ============================================================
-- A Purchase Requisition now identifies the supplier that the
-- resulting Purchase Order must inherit.
--
-- The column is initially nullable so existing requisitions are
-- not broken by the migration. New requisitions require a supplier
-- through application validation, and PO creation rejects any
-- approved requisition that has no supplier.
-- ============================================================

ALTER TABLE purchase_requisitions
    ADD COLUMN supplier_id UUID;

ALTER TABLE purchase_requisitions
    ADD CONSTRAINT fk_purchase_requisition_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id);

CREATE INDEX idx_purchase_requisitions_supplier
    ON purchase_requisitions(supplier_id);

COMMENT ON COLUMN purchase_requisitions.supplier_id IS
    'Supplier selected on the Purchase Requisition and inherited by the resulting Purchase Order.';
