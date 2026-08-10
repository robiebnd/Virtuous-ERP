ALTER TABLE inventory_transactions
    ADD COLUMN IF NOT EXISTS from_bin_id UUID;

ALTER TABLE inventory_transactions
    ADD COLUMN IF NOT EXISTS to_bin_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_inventory_transactions_from_bin'
    ) THEN
        ALTER TABLE inventory_transactions
            ADD CONSTRAINT fk_inventory_transactions_from_bin
            FOREIGN KEY (from_bin_id)
            REFERENCES bins(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_inventory_transactions_to_bin'
    ) THEN
        ALTER TABLE inventory_transactions
            ADD CONSTRAINT fk_inventory_transactions_to_bin
            FOREIGN KEY (to_bin_id)
            REFERENCES bins(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_inventory_transactions_from_bin
    ON inventory_transactions(from_bin_id);

CREATE INDEX IF NOT EXISTS idx_inventory_transactions_to_bin
    ON inventory_transactions(to_bin_id);
