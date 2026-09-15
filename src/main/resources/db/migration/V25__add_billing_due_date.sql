ALTER TABLE billing_documents
    ADD COLUMN IF NOT EXISTS due_date TIMESTAMP;

UPDATE billing_documents
SET due_date = billing_date + INTERVAL '30 days'
WHERE due_date IS NULL;

ALTER TABLE billing_documents
    ALTER COLUMN due_date SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_billing_documents_due_date
    ON billing_documents(due_date);
