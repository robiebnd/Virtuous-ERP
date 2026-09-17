-- Align the original V43 vendor payment table with the VendorPayment entity.
-- V43 already contains version, so this migration is intentionally additive.

ALTER TABLE vendor_payments
    ADD COLUMN IF NOT EXISTS payment_method VARCHAR(30);

ALTER TABLE vendor_payments
    ADD COLUMN IF NOT EXISTS reference_number VARCHAR(100);

ALTER TABLE vendor_payments
    ADD COLUMN IF NOT EXISTS created_by UUID REFERENCES users(id);

ALTER TABLE vendor_payments
    ADD COLUMN IF NOT EXISTS approved_by UUID REFERENCES users(id);

ALTER TABLE vendor_payments
    ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP;

ALTER TABLE vendor_payments
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

-- Preserve values from the original V43 reference column where present.
UPDATE vendor_payments
SET reference_number = reference
WHERE reference_number IS NULL
  AND reference IS NOT NULL;

-- Existing rows need a valid value before the application starts creating payments.
UPDATE vendor_payments
SET payment_method = 'BANK_TRANSFER'
WHERE payment_method IS NULL;

ALTER TABLE vendor_payments
    ALTER COLUMN payment_method SET DEFAULT 'BANK_TRANSFER';

ALTER TABLE vendor_payments
    ALTER COLUMN payment_method SET NOT NULL;
