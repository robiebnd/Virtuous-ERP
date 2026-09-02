CREATE TABLE sales_organizations (
    id UUID PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    country VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT
);

CREATE TABLE distribution_channels (
    id UUID PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT
);

CREATE TABLE divisions (
    id UUID PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT
);

CREATE TABLE sales_areas (
    id UUID PRIMARY KEY,
    sales_organization_id UUID NOT NULL REFERENCES sales_organizations(id),
    distribution_channel_id UUID NOT NULL REFERENCES distribution_channels(id),
    division_id UUID NOT NULL REFERENCES divisions(id),
    code VARCHAR(60) NOT NULL UNIQUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    CONSTRAINT uk_sales_area_combination UNIQUE (sales_organization_id, distribution_channel_id, division_id)
);

CREATE TABLE shipping_points (
    id UUID PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    address VARCHAR(300),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT
);

CREATE TABLE sales_offices (
    id UUID PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    sales_organization_id UUID NOT NULL REFERENCES sales_organizations(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT
);

CREATE TABLE sales_groups (
    id UUID PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    sales_office_id UUID NOT NULL REFERENCES sales_offices(id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT
);

CREATE TABLE customer_sales_areas (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id),
    sales_area_id UUID NOT NULL REFERENCES sales_areas(id),
    sales_office_id UUID REFERENCES sales_offices(id),
    sales_group_id UUID REFERENCES sales_groups(id),
    payment_terms VARCHAR(50),
    customer_pricing_group VARCHAR(30),
    delivery_priority INTEGER,
    shipping_condition VARCHAR(50),
    credit_limit NUMERIC(18,2),
    credit_exposure NUMERIC(18,2) NOT NULL DEFAULT 0,
    credit_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    CONSTRAINT uk_customer_sales_area UNIQUE (customer_id, sales_area_id)
);

CREATE TABLE product_sales_data (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL REFERENCES products(id),
    sales_area_id UUID NOT NULL REFERENCES sales_areas(id),
    sales_unit VARCHAR(30),
    tax_classification VARCHAR(30),
    item_category_group VARCHAR(30),
    delivering_plant_id UUID REFERENCES warehouses(id),
    shipping_point_id UUID REFERENCES shipping_points(id),
    minimum_order_quantity NUMERIC(18,2) NOT NULL DEFAULT 0,
    sales_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    CONSTRAINT uk_product_sales_area UNIQUE (product_id, sales_area_id)
);

CREATE TABLE customer_material_info (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id),
    product_id UUID NOT NULL REFERENCES products(id),
    customer_material_number VARCHAR(100) NOT NULL,
    customer_description VARCHAR(255),
    customer_unit VARCHAR(30),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    CONSTRAINT uk_customer_material UNIQUE (customer_id, product_id),
    CONSTRAINT uk_customer_material_number UNIQUE (customer_id, customer_material_number)
);

CREATE TABLE pricing_conditions (
    id UUID PRIMARY KEY,
    condition_type VARCHAR(30) NOT NULL,
    sales_area_id UUID REFERENCES sales_areas(id),
    customer_id UUID REFERENCES customers(id),
    product_id UUID REFERENCES products(id),
    valid_from TIMESTAMP NOT NULL,
    valid_to TIMESTAMP NOT NULL,
    rate NUMERIC(18,6) NOT NULL,
    rate_type VARCHAR(20) NOT NULL DEFAULT 'AMOUNT',
    currency VARCHAR(3) DEFAULT 'USD',
    priority INTEGER NOT NULL DEFAULT 100,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT,
    CONSTRAINT ck_pricing_validity CHECK (valid_to >= valid_from)
);

CREATE INDEX idx_customer_sales_area_customer ON customer_sales_areas(customer_id);
CREATE INDEX idx_product_sales_data_product ON product_sales_data(product_id);
CREATE INDEX idx_customer_material_info_customer ON customer_material_info(customer_id);
CREATE INDEX idx_pricing_conditions_lookup ON pricing_conditions(condition_type, sales_area_id, customer_id, product_id, valid_from, valid_to);
