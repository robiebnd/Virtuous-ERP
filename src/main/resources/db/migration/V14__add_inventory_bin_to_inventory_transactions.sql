ALTER TABLE inventory_transactions
    ADD COLUMN IF NOT EXISTS inventory_bin_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_inventory_transactions_inventory_bin'
    ) THEN
        ALTER TABLE inventory_transactions
            ADD CONSTRAINT fk_inventory_transactions_inventory_bin
            FOREIGN KEY (inventory_bin_id)
            REFERENCES inventory_bins(id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_inventory_transactions_inventory_bin
    ON inventory_transactions(inventory_bin_id);

-- Existing transaction rows may predate the inventory_bin relationship.
-- Keep the column nullable for historical compatibility while new
-- transactions populate it through InventoryTransactionServiceImpl.
