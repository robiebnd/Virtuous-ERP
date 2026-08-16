ALTER TABLE purchase_requisitions
    ADD COLUMN IF NOT EXISTS currency VARCHAR(3);

ALTER TABLE purchase_orders
    ADD COLUMN IF NOT EXISTS currency VARCHAR(3);
