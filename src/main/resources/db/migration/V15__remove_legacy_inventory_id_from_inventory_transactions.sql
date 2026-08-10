-- ==========================================================
-- V15 - Remove legacy inventory_id from inventory transactions
-- ==========================================================
--
-- Inventory transactions now reference inventory_bins through
-- inventory_bin_id. The old inventory_id column is no longer part
-- of the application model, but older databases may still retain
-- it as a NOT NULL column and/or foreign key.
--
-- Remove any foreign-key constraints that depend on inventory_id
-- before dropping the obsolete column. This keeps the migration
-- safe across databases where the legacy constraint name differs.

DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    FOR constraint_name IN
        SELECT DISTINCT c.conname
        FROM pg_constraint c
        JOIN pg_class t
            ON t.oid = c.conrelid
        JOIN pg_attribute a
            ON a.attrelid = t.oid
        WHERE t.relname = 'inventory_transactions'
          AND a.attname = 'inventory_id'
          AND a.attnum = ANY (c.conkey)
          AND c.contype = 'f'
    LOOP
        EXECUTE format(
            'ALTER TABLE inventory_transactions DROP CONSTRAINT IF EXISTS %I',
            constraint_name
        );
    END LOOP;
END $$;

ALTER TABLE inventory_transactions
    DROP COLUMN IF EXISTS inventory_id;

-- Current inventory transaction relationships are represented by:
--   inventory_bin_id -> inventory_bins(id)
--   from_bin_id      -> bins(id)
--   to_bin_id        -> bins(id)
--
-- inventory_bin_id remains nullable for historical transactions,
-- while new transactions populate it through the inventory service.
