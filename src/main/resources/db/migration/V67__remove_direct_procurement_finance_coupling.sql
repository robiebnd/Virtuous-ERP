-- Remove the temporary direct Procurement -> Finance foreign-key traceability
-- introduced before the bounded-module integration contract was established.
DROP INDEX IF EXISTS uq_goods_receipt_accounting_document;
DROP INDEX IF EXISTS uq_vendor_invoice_accounting_document;
DROP INDEX IF EXISTS idx_goods_receipts_accounting_document;
DROP INDEX IF EXISTS idx_vendor_invoices_accounting_document;

ALTER TABLE goods_receipts
    DROP COLUMN IF EXISTS accounting_document_id;

ALTER TABLE vendor_invoices
    DROP COLUMN IF EXISTS accounting_document_id;
