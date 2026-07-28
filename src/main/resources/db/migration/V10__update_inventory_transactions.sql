ALTER TABLE inventory_transactions
ADD COLUMN IF NOT EXISTS balance_after NUMERIC(18,2);

UPDATE inventory_transactions
SET balance_after = 0
WHERE balance_after IS NULL;

ALTER TABLE inventory_transactions
ALTER COLUMN balance_after SET NOT NULL;

ALTER TABLE inventory_transactions
ADD COLUMN IF NOT EXISTS reference_type VARCHAR(50);

UPDATE inventory_transactions
SET reference_type = 'UNKNOWN'
WHERE reference_type IS NULL;

ALTER TABLE inventory_transactions
ALTER COLUMN reference_type SET NOT NULL;

ALTER TABLE inventory_transactions
ADD COLUMN IF NOT EXISTS performed_by UUID;

ALTER TABLE inventory_transactions
ADD CONSTRAINT fk_inventory_transactions_performed_by
FOREIGN KEY (performed_by)
REFERENCES users(id);