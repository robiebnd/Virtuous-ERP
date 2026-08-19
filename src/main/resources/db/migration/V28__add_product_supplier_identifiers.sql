CREATE TABLE product_supplier_identifiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL,
    supplier_id UUID NOT NULL,
    supplier_item_code VARCHAR(100) NOT NULL,
    supplier_item_name VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_product_supplier_identifiers_product
        FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_product_supplier_identifiers_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT uq_product_supplier_identifier
        UNIQUE (supplier_id, supplier_item_code)
);

CREATE INDEX idx_product_supplier_identifiers_product
    ON product_supplier_identifiers(product_id);

CREATE INDEX idx_product_supplier_identifiers_supplier
    ON product_supplier_identifiers(supplier_id);
