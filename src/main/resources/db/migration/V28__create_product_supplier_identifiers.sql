CREATE TABLE product_supplier_identifiers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL,
    product_id UUID NOT NULL,
    supplier_item_code VARCHAR(100) NOT NULL,
    supplier_item_name VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_product_supplier_identifier_code
        UNIQUE (supplier_id, supplier_item_code),
    CONSTRAINT fk_product_supplier_identifier_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_product_supplier_identifier_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_product_supplier_identifier_product
    ON product_supplier_identifiers(product_id);
