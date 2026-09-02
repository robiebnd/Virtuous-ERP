CREATE TABLE sales_inquiries (
    id UUID PRIMARY KEY,
    inquiry_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customers(id),
    sales_area_id UUID REFERENCES sales_areas(id),
    inquiry_date TIMESTAMP NOT NULL,
    requested_valid_until DATE,
    status VARCHAR(30) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE sales_inquiry_lines (
    id UUID PRIMARY KEY,
    inquiry_id UUID NOT NULL REFERENCES sales_inquiries(id) ON DELETE CASCADE,
    line_number INTEGER NOT NULL,
    sku VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    quantity NUMERIC(19,4) NOT NULL,
    requested_delivery_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_sales_inquiry_line UNIQUE (inquiry_id, line_number)
);

CREATE TABLE sales_quotations (
    id UUID PRIMARY KEY,
    quotation_number VARCHAR(50) NOT NULL UNIQUE,
    inquiry_id UUID REFERENCES sales_inquiries(id),
    customer_id UUID NOT NULL REFERENCES customers(id),
    sales_area_id UUID REFERENCES sales_areas(id),
    quotation_date TIMESTAMP NOT NULL,
    valid_from DATE NOT NULL,
    valid_to DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    subtotal NUMERIC(19,4) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    total_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    notes TEXT,
    converted_order_number VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_quotation_validity CHECK (valid_to >= valid_from)
);

CREATE TABLE sales_quotation_lines (
    id UUID PRIMARY KEY,
    quotation_id UUID NOT NULL REFERENCES sales_quotations(id) ON DELETE CASCADE,
    line_number INTEGER NOT NULL,
    sku VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    quantity NUMERIC(19,4) NOT NULL,
    unit_price NUMERIC(19,4) NOT NULL,
    discount_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(19,4) NOT NULL DEFAULT 0,
    line_total NUMERIC(19,4) NOT NULL DEFAULT 0,
    requested_delivery_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_sales_quotation_line UNIQUE (quotation_id, line_number)
);

CREATE INDEX idx_sales_inquiries_customer ON sales_inquiries(customer_id);
CREATE INDEX idx_sales_inquiries_status ON sales_inquiries(status);
CREATE INDEX idx_sales_quotations_customer ON sales_quotations(customer_id);
CREATE INDEX idx_sales_quotations_status ON sales_quotations(status);
CREATE INDEX idx_sales_quotations_valid_to ON sales_quotations(valid_to);
