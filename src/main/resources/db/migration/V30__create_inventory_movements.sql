CREATE TABLE IF NOT EXISTS inventory_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    warehouse_id UUID NOT NULL,
    from_bin_id UUID NULL,
    to_bin_id UUID NULL,
    product_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity NUMERIC(19,4) NOT NULL CHECK (quantity > 0),
    movement_type VARCHAR(30) NOT NULL,
    reference_type VARCHAR(30) NOT NULL,
    reference_id UUID NOT NULL,
    reference_number VARCHAR(100),
    performed_by_id UUID,
    remarks TEXT,
    movement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_inventory_movement_sku
    ON inventory_movements (sku);

CREATE INDEX IF NOT EXISTS idx_inventory_movement_warehouse
    ON inventory_movements (warehouse_id);

CREATE INDEX IF NOT EXISTS idx_inventory_movement_reference
    ON inventory_movements (reference_type, reference_id);

CREATE INDEX IF NOT EXISTS idx_inventory_movement_date
    ON inventory_movements (movement_date);
