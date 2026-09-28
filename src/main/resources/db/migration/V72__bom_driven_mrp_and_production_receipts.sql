CREATE TABLE IF NOT EXISTS mfg_mrp_component_requirements (
 id UUID PRIMARY KEY,
 created_at TIMESTAMP,
 updated_at TIMESTAMP,
 mrp_plan_id UUID NOT NULL REFERENCES mfg_mrp_plans(id),
 component_product_id UUID NOT NULL REFERENCES products(id),
 parent_product_id UUID NOT NULL REFERENCES products(id),
 gross_requirement NUMERIC(18,2) NOT NULL,
 available_stock NUMERIC(18,2) NOT NULL,
 open_supply NUMERIC(18,2) NOT NULL,
 net_requirement NUMERIC(18,2) NOT NULL,
 planned_order_quantity NUMERIC(18,2) NOT NULL,
 generated_production_order_id UUID REFERENCES mfg_production_orders(id),
 bom_level INTEGER NOT NULL
);
ALTER TABLE mfg_production_orders ADD COLUMN IF NOT EXISTS received_quantity NUMERIC(18,2) NOT NULL DEFAULT 0;
CREATE INDEX IF NOT EXISTS idx_mrp_component_plan ON mfg_mrp_component_requirements(mrp_plan_id,bom_level);
CREATE INDEX IF NOT EXISTS idx_mrp_component_product ON mfg_mrp_component_requirements(component_product_id);