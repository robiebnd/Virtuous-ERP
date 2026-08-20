CREATE TABLE purchasing_info_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_supplier_identifier_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    currency VARCHAR(3) NOT NULL,
    last_purchase_price NUMERIC(19, 4),
    standard_order_quantity NUMERIC(19, 4),
    planned_delivery_days INTEGER,
    valid_from DATE,
    valid_to DATE,
    regular_supplier BOOLEAN NOT NULL DEFAULT FALSE,
    automatic_sourcing BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pir_supplier_product_identifier
        FOREIGN KEY (product_supplier_identifier_id)
        REFERENCES product_supplier_identifiers(id),
    CONSTRAINT fk_pir_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),
    CONSTRAINT ck_pir_currency
        CHECK (currency ~ '^[A-Z]{3}$'),
    CONSTRAINT ck_pir_prices_non_negative
        CHECK (last_purchase_price IS NULL OR last_purchase_price >= 0),
    CONSTRAINT ck_pir_order_qty_positive
        CHECK (standard_order_quantity IS NULL OR standard_order_quantity > 0),
    CONSTRAINT ck_pir_delivery_days_non_negative
        CHECK (planned_delivery_days IS NULL OR planned_delivery_days >= 0),
    CONSTRAINT ck_pir_validity
        CHECK (valid_to IS NULL OR valid_from IS NULL OR valid_to >= valid_from)
);

CREATE UNIQUE INDEX uq_pir_supplier_product_warehouse
    ON purchasing_info_records(product_supplier_identifier_id, warehouse_id);

CREATE INDEX idx_pir_product_supplier_identifier
    ON purchasing_info_records(product_supplier_identifier_id);

CREATE INDEX idx_pir_warehouse
    ON purchasing_info_records(warehouse_id);
