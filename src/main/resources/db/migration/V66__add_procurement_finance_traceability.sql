ALTER TABLE goods_receipts
    ADD COLUMN IF NOT EXISTS accounting_document_id UUID;

ALTER TABLE vendor_invoices
    ADD COLUMN IF NOT EXISTS accounting_document_id UUID;

CREATE UNIQUE INDEX IF NOT EXISTS uq_goods_receipt_accounting_document
    ON goods_receipts(accounting_document_id)
    WHERE accounting_document_id IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uq_vendor_invoice_accounting_document
    ON vendor_invoices(accounting_document_id)
    WHERE accounting_document_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_goods_receipts_accounting_document
    ON goods_receipts(accounting_document_id);

CREATE INDEX IF NOT EXISTS idx_vendor_invoices_accounting_document
    ON vendor_invoices(accounting_document_id);
