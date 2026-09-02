CREATE TABLE IF NOT EXISTS sales_orders (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    remarks VARCHAR(3000),
    order_number VARCHAR(40) NOT NULL UNIQUE,
    customer_code VARCHAR(40) NOT NULL,
    sales_organization VARCHAR(20) NOT NULL,
    distribution_channel VARCHAR(20) NOT NULL,
    division VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    sap_order_number VARCHAR(40) UNIQUE,
    order_date TIMESTAMP NOT NULL,
    total_amount NUMERIC(19,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS sales_order_items (
    id UUID PRIMARY KEY,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    sales_order_id UUID NOT NULL,
    item_number INTEGER NOT NULL,
    material_code VARCHAR(40) NOT NULL,
    quantity NUMERIC(19,3) NOT NULL,
    unit_price NUMERIC(19,2),
    net_value NUMERIC(19,2),
    CONSTRAINT fk_sales_order_items_order
        FOREIGN KEY (sales_order_id) REFERENCES sales_orders(id) ON DELETE CASCADE,
    CONSTRAINT uq_sales_order_item_number
        UNIQUE (sales_order_id, item_number)
);

CREATE INDEX IF NOT EXISTS idx_sales_orders_customer_code
    ON sales_orders(customer_code);

CREATE INDEX IF NOT EXISTS idx_sales_orders_status
    ON sales_orders(status);

CREATE INDEX IF NOT EXISTS idx_sales_order_items_material_code
    ON sales_order_items(material_code);
