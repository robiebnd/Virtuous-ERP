ALTER TABLE purchase_orders
    ADD COLUMN IF NOT EXISTS approved_at TIMESTAMP;
