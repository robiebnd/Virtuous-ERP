-- Complete QM disposition stock buckets and MRP execution data
ALTER TABLE inventory_bins ADD COLUMN IF NOT EXISTS quantity_quality NUMERIC(18,2) NOT NULL DEFAULT 0;
ALTER TABLE inventory_bins ADD COLUMN IF NOT EXISTS quantity_blocked NUMERIC(18,2) NOT NULL DEFAULT 0;

ALTER TABLE mfg_mrp_plans ADD COLUMN IF NOT EXISTS product_id UUID REFERENCES products(id);
ALTER TABLE mfg_mrp_plans ADD COLUMN IF NOT EXISTS gross_requirement NUMERIC(18,2);
ALTER TABLE mfg_mrp_plans ADD COLUMN IF NOT EXISTS available_stock NUMERIC(18,2);
ALTER TABLE mfg_mrp_plans ADD COLUMN IF NOT EXISTS safety_stock NUMERIC(18,2);
ALTER TABLE mfg_mrp_plans ADD COLUMN IF NOT EXISTS net_requirement NUMERIC(18,2);
ALTER TABLE mfg_mrp_plans ADD COLUMN IF NOT EXISTS planned_order_quantity NUMERIC(18,2);
ALTER TABLE mfg_mrp_plans ADD COLUMN IF NOT EXISTS generated_production_order_id UUID REFERENCES mfg_production_orders(id);

CREATE INDEX IF NOT EXISTS idx_inventory_bins_quality ON inventory_bins(product_id, warehouse_id, quantity_quality);
CREATE INDEX IF NOT EXISTS idx_inventory_bins_blocked ON inventory_bins(product_id, warehouse_id, quantity_blocked);
CREATE INDEX IF NOT EXISTS idx_mfg_mrp_product ON mfg_mrp_plans(product_id, plant_code);
