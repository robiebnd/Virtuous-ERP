CREATE TABLE supplier_quotations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_number VARCHAR(100) NOT NULL,
    supplier_id UUID NOT NULL,
    purchase_requisition_id UUID NOT NULL,
    quotation_date DATE,
    original_file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'UPLOADED',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_supplier_quotations_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_supplier_quotations_pr
        FOREIGN KEY (purchase_requisition_id) REFERENCES purchase_requisitions(id)
);

CREATE INDEX idx_supplier_quotations_pr
    ON supplier_quotations(purchase_requisition_id);

CREATE INDEX idx_supplier_quotations_supplier
    ON supplier_quotations(supplier_id);
