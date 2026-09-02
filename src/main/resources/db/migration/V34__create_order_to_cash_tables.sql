CREATE TABLE customers (
    id UUID PRIMARY KEY,
    customer_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(50),
    billing_address TEXT,
    shipping_address TEXT,
    payment_terms VARCHAR(100),
    credit_limit NUMERIC(18,2),
    credit_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE sales_orders (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customers(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    order_date TIMESTAMP NOT NULL,
    requested_delivery_date TIMESTAMP,
    status VARCHAR(30) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    payment_terms VARCHAR(100),
    subtotal NUMERIC(18,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    credit_blocked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE sales_order_lines (
    id UUID PRIMARY KEY,
    sales_order_id UUID NOT NULL REFERENCES sales_orders(id) ON DELETE CASCADE,
    line_number INTEGER NOT NULL,
    product_id UUID NOT NULL REFERENCES products(id),
    quantity NUMERIC(18,2) NOT NULL,
    unit_price NUMERIC(18,2) NOT NULL,
    discount_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    line_total NUMERIC(18,2) NOT NULL,
    quantity_delivered NUMERIC(18,2) NOT NULL DEFAULT 0,
    quantity_billed NUMERIC(18,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_sales_order_line UNIQUE (sales_order_id, line_number)
);

CREATE TABLE outbound_deliveries (
    id UUID PRIMARY KEY,
    delivery_number VARCHAR(50) NOT NULL UNIQUE,
    sales_order_id UUID NOT NULL REFERENCES sales_orders(id),
    warehouse_id UUID NOT NULL REFERENCES warehouses(id),
    delivery_date TIMESTAMP NOT NULL,
    status VARCHAR(30) NOT NULL,
    picked BOOLEAN NOT NULL DEFAULT FALSE,
    packed BOOLEAN NOT NULL DEFAULT FALSE,
    goods_issue_posted BOOLEAN NOT NULL DEFAULT FALSE,
    goods_issue_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE outbound_delivery_lines (
    id UUID PRIMARY KEY,
    delivery_id UUID NOT NULL REFERENCES outbound_deliveries(id) ON DELETE CASCADE,
    sales_order_line_id UUID NOT NULL REFERENCES sales_order_lines(id),
    product_id UUID NOT NULL REFERENCES products(id),
    bin_id UUID REFERENCES bins(id),
    quantity NUMERIC(18,2) NOT NULL,
    picked_quantity NUMERIC(18,2) NOT NULL DEFAULT 0,
    packed_quantity NUMERIC(18,2) NOT NULL DEFAULT 0,
    issued_quantity NUMERIC(18,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE customer_invoices (
    id UUID PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customers(id),
    sales_order_id UUID NOT NULL REFERENCES sales_orders(id),
    delivery_id UUID REFERENCES outbound_deliveries(id),
    invoice_date TIMESTAMP NOT NULL,
    due_date TIMESTAMP,
    status VARCHAR(30) NOT NULL,
    currency VARCHAR(10) NOT NULL DEFAULT 'USD',
    subtotal NUMERIC(18,2) NOT NULL DEFAULT 0,
    discount_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    tax_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    total_amount NUMERIC(18,2) NOT NULL DEFAULT 0,
    amount_paid NUMERIC(18,2) NOT NULL DEFAULT 0,
    balance_due NUMERIC(18,2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE customer_invoice_lines (
    id UUID PRIMARY KEY,
    invoice_id UUID NOT NULL REFERENCES customer_invoices(id) ON DELETE CASCADE,
    sales_order_line_id UUID NOT NULL REFERENCES sales_order_lines(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity NUMERIC(18,2) NOT NULL,
    unit_price NUMERIC(18,2) NOT NULL,
    line_total NUMERIC(18,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE customer_payments (
    id UUID PRIMARY KEY,
    payment_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL REFERENCES customers(id),
    invoice_id UUID NOT NULL REFERENCES customer_invoices(id),
    payment_date TIMESTAMP NOT NULL,
    amount NUMERIC(18,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    reference VARCHAR(100),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_sales_orders_customer ON sales_orders(customer_id);
CREATE INDEX idx_sales_orders_status ON sales_orders(status);
CREATE INDEX idx_sales_order_lines_product ON sales_order_lines(product_id);
CREATE INDEX idx_deliveries_sales_order ON outbound_deliveries(sales_order_id);
CREATE INDEX idx_deliveries_status ON outbound_deliveries(status);
CREATE INDEX idx_invoice_customer ON customer_invoices(customer_id);
CREATE INDEX idx_invoice_status ON customer_invoices(status);
CREATE INDEX idx_payment_invoice ON customer_payments(invoice_id);
