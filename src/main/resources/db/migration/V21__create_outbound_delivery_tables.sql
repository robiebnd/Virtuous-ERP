CREATE TABLE IF NOT EXISTS outbound_deliveries (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    remarks VARCHAR(3000),
    delivery_number VARCHAR(40) NOT NULL UNIQUE,
    sales_order_id UUID NOT NULL,
    customer_code VARCHAR(40) NOT NULL,
    shipping_point VARCHAR(40) NOT NULL,
    requested_delivery_date TIMESTAMP,
    status VARCHAR(30) NOT NULL,
    picked_at TIMESTAMP,
    packed_at TIMESTAMP,
    goods_issue_at TIMESTAMP,
    CONSTRAINT fk_outbound_delivery_sales_order
        FOREIGN KEY (sales_order_id) REFERENCES sales_orders(id)
);

CREATE TABLE IF NOT EXISTS outbound_delivery_items (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    delivery_id UUID NOT NULL,
    item_number INTEGER NOT NULL,
    material_code VARCHAR(40) NOT NULL,
    ordered_quantity NUMERIC(19,3) NOT NULL,
    picked_quantity NUMERIC(19,3) NOT NULL DEFAULT 0,
    packed_quantity NUMERIC(19,3) NOT NULL DEFAULT 0,
    delivered_quantity NUMERIC(19,3) NOT NULL DEFAULT 0,
    CONSTRAINT fk_outbound_delivery_items_delivery
        FOREIGN KEY (delivery_id) REFERENCES outbound_deliveries(id) ON DELETE CASCADE,
    CONSTRAINT uq_outbound_delivery_item_number
        UNIQUE (delivery_id, item_number)
);

CREATE INDEX IF NOT EXISTS idx_outbound_deliveries_sales_order
    ON outbound_deliveries(sales_order_id);

CREATE INDEX IF NOT EXISTS idx_outbound_deliveries_status
    ON outbound_deliveries(status);

CREATE INDEX IF NOT EXISTS idx_outbound_delivery_items_material_code
    ON outbound_delivery_items(material_code);
