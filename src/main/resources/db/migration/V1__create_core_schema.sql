-- ==========================================================
-- V1__create_core_schema.sql
-- PART 1 - EXTENSIONS & SECURITY
-- ==========================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==========================================================
-- ROLES
-- ==========================================================

CREATE TABLE roles
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    name            VARCHAR(100) NOT NULL UNIQUE,

    description     VARCHAR(500),

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active          BOOLEAN NOT NULL DEFAULT TRUE,

    version         BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_roles_name
ON roles(name);

-- ==========================================================
-- PERMISSIONS
-- ==========================================================

CREATE TABLE permissions
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code            VARCHAR(150) NOT NULL UNIQUE,

    name            VARCHAR(200) NOT NULL,

    description     VARCHAR(500),

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active          BOOLEAN NOT NULL DEFAULT TRUE,

    version         BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_permissions_code
ON permissions(code);

-- ==========================================================
-- WAREHOUSES
-- ==========================================================

CREATE TABLE warehouses
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code            VARCHAR(50) NOT NULL UNIQUE,

    name            VARCHAR(200) NOT NULL,

    address         VARCHAR(500),

    city            VARCHAR(150),

    country         VARCHAR(150),

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active          BOOLEAN NOT NULL DEFAULT TRUE,

    version         BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_warehouse_code
ON warehouses(code);

-- ==========================================================
-- USERS
-- ==========================================================

CREATE TABLE users
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    username                VARCHAR(100) NOT NULL UNIQUE,

    password                VARCHAR(255) NOT NULL,

    email                   VARCHAR(255) NOT NULL UNIQUE,

    first_name              VARCHAR(100),

    last_name               VARCHAR(100),

    phone_number            VARCHAR(50),

    account_locked          BOOLEAN NOT NULL DEFAULT FALSE,

    enabled                 BOOLEAN NOT NULL DEFAULT TRUE,

    default_warehouse_id    UUID,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_users_default_warehouse
        FOREIGN KEY (default_warehouse_id)
        REFERENCES warehouses(id)
);

CREATE INDEX idx_users_username
ON users(username);

CREATE INDEX idx_users_email
ON users(email);

CREATE INDEX idx_users_default_warehouse
ON users(default_warehouse_id);

-- ==========================================================
-- USER ROLES
-- ==========================================================

CREATE TABLE user_roles
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id         UUID NOT NULL,

    role_id         UUID NOT NULL,

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active          BOOLEAN NOT NULL DEFAULT TRUE,

    version         BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uq_user_role
        UNIQUE(user_id, role_id),

    CONSTRAINT fk_user_role_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_role_role
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_user_roles_user
ON user_roles(user_id);

CREATE INDEX idx_user_roles_role
ON user_roles(role_id);

-- ==========================================================
-- ROLE PERMISSIONS
-- ==========================================================

CREATE TABLE role_permissions
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    role_id             UUID NOT NULL,

    permission_id       UUID NOT NULL,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active              BOOLEAN NOT NULL DEFAULT TRUE,

    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uq_role_permission
        UNIQUE(role_id, permission_id),

    CONSTRAINT fk_role_permission_role
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permission_permission
        FOREIGN KEY(permission_id)
        REFERENCES permissions(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_role_permissions_role
ON role_permissions(role_id);

CREATE INDEX idx_role_permissions_permission
ON role_permissions(permission_id);

-- ==========================================================
-- PART 2
-- PRODUCT MASTER
-- ==========================================================

-- ==========================================================
-- UNIT OF MEASURE
-- ==========================================================

CREATE TABLE unit_of_measure
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code            VARCHAR(20) NOT NULL UNIQUE,

    name            VARCHAR(100) NOT NULL,

    description     VARCHAR(255),

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active          BOOLEAN NOT NULL DEFAULT TRUE,

    version         BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_uom_code
ON unit_of_measure(code);

CREATE INDEX idx_uom_name
ON unit_of_measure(name);

-- ==========================================================
-- PRODUCT CATEGORIES
-- ==========================================================

CREATE TABLE product_categories
(
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code            VARCHAR(30) NOT NULL UNIQUE,

    name            VARCHAR(100) NOT NULL UNIQUE,

    description     VARCHAR(255),

    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active          BOOLEAN NOT NULL DEFAULT TRUE,

    version         BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_product_category_code
ON product_categories(code);

CREATE INDEX idx_product_category_name
ON product_categories(name);

-- ==========================================================
-- SUPPLIERS
-- ==========================================================

CREATE TABLE suppliers
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code                VARCHAR(20) NOT NULL UNIQUE,

    name                VARCHAR(150) NOT NULL,

    contact_person      VARCHAR(100),

    phone               VARCHAR(30),

    email               VARCHAR(150),

    address             VARCHAR(255),

    city                VARCHAR(100),

    country             VARCHAR(100),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active              BOOLEAN NOT NULL DEFAULT TRUE,

    version             BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_supplier_code
ON suppliers(code);

CREATE INDEX idx_supplier_name
ON suppliers(name);

CREATE INDEX idx_supplier_email
ON suppliers(email);

-- ==========================================================
-- PRODUCTS
-- ==========================================================

CREATE TABLE products
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    sku                 VARCHAR(255) NOT NULL UNIQUE,

    name                VARCHAR(255) NOT NULL,

    description         TEXT,

    cost_price          NUMERIC(18,2),

    selling_price       NUMERIC(18,2),

    category_id         UUID,

    uom_id              UUID,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active              BOOLEAN NOT NULL DEFAULT TRUE,

    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_product_category
        FOREIGN KEY(category_id)
        REFERENCES product_categories(id),

    CONSTRAINT fk_product_uom
        FOREIGN KEY(uom_id)
        REFERENCES unit_of_measure(id)
);

CREATE INDEX idx_product_sku
ON products(sku);

CREATE INDEX idx_product_name
ON products(name);

CREATE INDEX idx_product_category
ON products(category_id);

CREATE INDEX idx_product_uom
ON products(uom_id);

-- ==========================================================
-- DOCUMENT SEQUENCES
-- ==========================================================

CREATE TABLE document_sequences
(
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    document_type VARCHAR(100) NOT NULL,

    prefix VARCHAR(20) NOT NULL,

    suffix VARCHAR(20),

    current_number BIGINT NOT NULL,

    padding INTEGER NOT NULL DEFAULT 6,

    financial_year INTEGER NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uq_document_sequence
        UNIQUE(document_type, financial_year)
);
/*
CREATE TABLE document_sequences
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    document_type       VARCHAR(100) NOT NULL,

    document_year       INTEGER NOT NULL,

    next_number         BIGINT NOT NULL DEFAULT 1,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uq_document_sequence
        UNIQUE(document_type, document_year)
);

CREATE INDEX idx_document_sequence
ON document_sequences(document_type, document_year);
*/
-- ==========================================================
-- PART 3
-- PURCHASING
-- ==========================================================

-- ==========================================================
-- PURCHASE REQUISITIONS
-- ==========================================================

CREATE TABLE purchase_requisitions
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    requisition_number          VARCHAR(50) NOT NULL UNIQUE,

    warehouse_id                UUID NOT NULL,

    requested_by                UUID,

    approved_by                 UUID,

    rejected_by                 UUID,

    cancelled_by                UUID,

    department                  VARCHAR(150),

    remarks                     TEXT,

    rejection_reason            TEXT,

    status                      VARCHAR(30) NOT NULL,

    submitted_at                TIMESTAMP,

    approved_at                 TIMESTAMP,

    rejected_at                 TIMESTAMP,

    cancelled_at                TIMESTAMP,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                      BOOLEAN NOT NULL DEFAULT TRUE,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_pr_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),

       CONSTRAINT fk_pr_requested_by
        FOREIGN KEY (requested_by)
        REFERENCES users(id),

    CONSTRAINT fk_pr_approved_by
        FOREIGN KEY (approved_by)
        REFERENCES users(id),

    CONSTRAINT fk_pr_rejected_by
        FOREIGN KEY (rejected_by)
        REFERENCES users(id),

    CONSTRAINT fk_pr_cancelled_by
        FOREIGN KEY (cancelled_by)
        REFERENCES users(id)
);

CREATE INDEX idx_pr_number
ON purchase_requisitions(requisition_number);

CREATE INDEX idx_pr_status
ON purchase_requisitions(status);

CREATE INDEX idx_pr_warehouse
ON purchase_requisitions(warehouse_id);

CREATE INDEX idx_pr_requested_by
ON purchase_requisitions(requested_by);

-- ==========================================================
-- PURCHASE REQUISITION LINES
-- ==========================================================

CREATE TABLE purchase_requisition_lines
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    purchase_requisition_id     UUID NOT NULL,

    product_id                  UUID NOT NULL,

    quantity                    NUMERIC(18,2) NOT NULL,

    estimated_unit_cost         NUMERIC(18,2),

    remarks                     VARCHAR(500),

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                      BOOLEAN NOT NULL DEFAULT TRUE,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_pr_line_pr
        FOREIGN KEY (purchase_requisition_id)
        REFERENCES purchase_requisitions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_pr_line_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);

CREATE INDEX idx_pr_line_pr
ON purchase_requisition_lines(purchase_requisition_id);

CREATE INDEX idx_pr_line_product
ON purchase_requisition_lines(product_id);

-- ==========================================================
-- PURCHASE ORDERS
-- ==========================================================

CREATE TABLE purchase_orders
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    po_number                   VARCHAR(50) NOT NULL UNIQUE,

    purchase_requisition_id      UUID,

    supplier_id                 UUID,

    warehouse_id                UUID NOT NULL,

    created_by                  UUID,

    submitted_by                UUID,

    approved_by                 UUID,

    cancelled_by                UUID,

    closed_by                   UUID,

    source                      VARCHAR(30) NOT NULL,

    status                      VARCHAR(30) NOT NULL,

    order_date                  TIMESTAMP NOT NULL,

    submitted_at                TIMESTAMP,

    approved_at                 TIMESTAMP,

    cancelled_at                TIMESTAMP,

    closed_at                   TIMESTAMP,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                      BOOLEAN NOT NULL DEFAULT TRUE,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_po_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES suppliers(id),

    CONSTRAINT fk_po_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_po_requisition
        FOREIGN KEY (purchase_requisition_id)
        REFERENCES purchase_requisitions(id),

    CONSTRAINT fk_po_created_by
        FOREIGN KEY (created_by)
        REFERENCES users(id),

    CONSTRAINT fk_po_submitted_by
        FOREIGN KEY (submitted_by)
        REFERENCES users(id),

    CONSTRAINT fk_po_approved_by
        FOREIGN KEY (approved_by)
        REFERENCES users(id),

    CONSTRAINT fk_po_cancelled_by
        FOREIGN KEY (cancelled_by)
        REFERENCES users(id),

    CONSTRAINT fk_po_closed_by
        FOREIGN KEY (closed_by)
        REFERENCES users(id)
);

CREATE INDEX idx_po_number
ON purchase_orders(po_number);

CREATE INDEX idx_po_supplier
ON purchase_orders(supplier_id);

CREATE INDEX idx_po_status
ON purchase_orders(status);

CREATE INDEX idx_po_warehouse
ON purchase_orders(warehouse_id);

CREATE INDEX idx_po_source
ON purchase_orders(source);

CREATE INDEX idx_po_requisition
ON purchase_orders(purchase_requisition_id);

-- ==========================================================
-- PURCHASE ORDER LINES
-- ==========================================================

CREATE TABLE purchase_order_lines
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    purchase_order_id       UUID NOT NULL,

    product_id              UUID NOT NULL,

    quantity                NUMERIC(18,2) NOT NULL,

    unit_price              NUMERIC(18,2) NOT NULL,

    line_total              NUMERIC(18,2),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_po_line_po
        FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_po_line_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);

CREATE INDEX idx_po_line_po
ON purchase_order_lines(purchase_order_id);

CREATE INDEX idx_po_line_product
ON purchase_order_lines(product_id);

-- ==========================================================
-- PART 4
-- INVENTORY & GOODS RECEIVING
-- ==========================================================

-- ==========================================================
-- INVENTORY
-- ==========================================================

CREATE TABLE inventory
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    warehouse_id            UUID NOT NULL,

    product_id              UUID NOT NULL,

    quantity_on_hand        NUMERIC(18,2) NOT NULL DEFAULT 0,

    quantity_reserved       NUMERIC(18,2) NOT NULL DEFAULT 0,

    quantity_available      NUMERIC(18,2) NOT NULL DEFAULT 0,

    average_cost            NUMERIC(18,2) NOT NULL DEFAULT 0,

    minimum_stock           NUMERIC(18,2) DEFAULT 0,

    maximum_stock           NUMERIC(18,2),

    reorder_level           NUMERIC(18,2),

    last_receipt_date       TIMESTAMP,

    last_issue_date         TIMESTAMP,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uq_inventory
        UNIQUE
        (
            warehouse_id,
            product_id
        ),

    CONSTRAINT fk_inventory_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_inventory_product
        FOREIGN KEY (product_id)
        REFERENCES products(id)
);

CREATE INDEX idx_inventory_product
ON inventory(product_id);

CREATE INDEX idx_inventory_warehouse
ON inventory(warehouse_id);

CREATE INDEX idx_inventory_available
ON inventory(quantity_available);

-- ==========================================================
-- INVENTORY TRANSACTIONS
-- ==========================================================

CREATE TABLE inventory_transactions
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    warehouse_id            UUID NOT NULL,

    product_id              UUID NOT NULL,

    reference_number        VARCHAR(100),

    reference_type          VARCHAR(50) NOT NULL,

    transaction_type        VARCHAR(50) NOT NULL,

    quantity                NUMERIC(18,2) NOT NULL,

    unit_cost               NUMERIC(18,2),

    balance_after           NUMERIC(18,2),

    remarks                 VARCHAR(500),

    transaction_date        TIMESTAMP NOT NULL,

    created_by              UUID,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_inventory_transaction_product
        FOREIGN KEY(product_id)
        REFERENCES products(id),

    CONSTRAINT fk_inventory_transaction_warehouse
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_inventory_transaction_user
        FOREIGN KEY(created_by)
        REFERENCES users(id)
);

CREATE INDEX idx_inventory_transaction_product
ON inventory_transactions(product_id);

CREATE INDEX idx_inventory_transaction_warehouse
ON inventory_transactions(warehouse_id);

CREATE INDEX idx_inventory_transaction_date
ON inventory_transactions(transaction_date);

CREATE INDEX idx_inventory_transaction_reference
ON inventory_transactions(reference_number);
-- ==========================================================
-- GOODS RECEIPTS
-- ==========================================================

CREATE TABLE goods_receipts
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    grn_number                  VARCHAR(50) NOT NULL UNIQUE,

    purchase_order_id           UUID NOT NULL,

    warehouse_id                UUID NOT NULL,

    received_by                 UUID,

    approved_by                 UUID,

    status                      VARCHAR(30) NOT NULL,

    supplier_delivery_note      VARCHAR(100),

    remarks                     TEXT,

    received_date               TIMESTAMP NOT NULL,

    approved_at                 TIMESTAMP,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                      BOOLEAN NOT NULL DEFAULT TRUE,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_grn_po
        FOREIGN KEY(purchase_order_id)
        REFERENCES purchase_orders(id),

    CONSTRAINT fk_grn_warehouse
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_grn_received_by
        FOREIGN KEY(received_by)
        REFERENCES users(id),

    CONSTRAINT fk_grn_approved_by
        FOREIGN KEY(approved_by)
        REFERENCES users(id)
);

CREATE INDEX idx_grn_number
ON goods_receipts(grn_number);

CREATE INDEX idx_grn_status
ON goods_receipts(status);

CREATE INDEX idx_grn_po
ON goods_receipts(purchase_order_id);

-- ==========================================================
-- GOODS RECEIPT LINES
-- ==========================================================

CREATE TABLE goods_receipt_lines
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    goods_receipt_id        UUID NOT NULL,

    purchase_order_line_id  UUID,

    product_id              UUID NOT NULL,

    ordered_quantity        NUMERIC(18,2),

    received_quantity       NUMERIC(18,2) NOT NULL,

    accepted_quantity       NUMERIC(18,2),

    rejected_quantity       NUMERIC(18,2),

    unit_cost               NUMERIC(18,2),

    remarks                 VARCHAR(500),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_grn_line_grn
        FOREIGN KEY(goods_receipt_id)
        REFERENCES goods_receipts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_grn_line_po
        FOREIGN KEY(purchase_order_line_id)
        REFERENCES purchase_order_lines(id),

    CONSTRAINT fk_grn_line_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
);

CREATE INDEX idx_grn_line_grn
ON goods_receipt_lines(goods_receipt_id);

CREATE INDEX idx_grn_line_product
ON goods_receipt_lines(product_id);

-- ==========================================================
-- PART 5
-- STOCK CONTROL
-- ==========================================================

-- ==========================================================
-- STOCK TRANSFERS
-- ==========================================================

CREATE TABLE stock_transfers
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    transfer_number             VARCHAR(50) NOT NULL UNIQUE,

    from_warehouse_id           UUID NOT NULL,

    to_warehouse_id             UUID NOT NULL,

    requested_by                UUID,

    approved_by                 UUID,

    issued_by                   UUID,

    received_by                 UUID,

    status                      VARCHAR(30) NOT NULL,

    remarks                     TEXT,

    transfer_date               TIMESTAMP NOT NULL,

    approved_at                 TIMESTAMP,

    issued_at                   TIMESTAMP,

    received_at                 TIMESTAMP,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                      BOOLEAN NOT NULL DEFAULT TRUE,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_transfer_from_wh
        FOREIGN KEY(from_warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_transfer_to_wh
        FOREIGN KEY(to_warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_transfer_requested_by
        FOREIGN KEY(requested_by)
        REFERENCES users(id),

    CONSTRAINT fk_transfer_approved_by
        FOREIGN KEY(approved_by)
        REFERENCES users(id),

    CONSTRAINT fk_transfer_issued_by
        FOREIGN KEY(issued_by)
        REFERENCES users(id),

    CONSTRAINT fk_transfer_received_by
        FOREIGN KEY(received_by)
        REFERENCES users(id)
);

CREATE INDEX idx_transfer_number
ON stock_transfers(transfer_number);

CREATE INDEX idx_transfer_status
ON stock_transfers(status);

CREATE INDEX idx_transfer_from_wh
ON stock_transfers(from_warehouse_id);

CREATE INDEX idx_transfer_to_wh
ON stock_transfers(to_warehouse_id);

-- ==========================================================
-- STOCK TRANSFER LINES
-- ==========================================================

CREATE TABLE stock_transfer_lines
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    stock_transfer_id       UUID NOT NULL,

    product_id              UUID NOT NULL,

    quantity                NUMERIC(18,2) NOT NULL,

    unit_cost               NUMERIC(18,2),

    remarks                 VARCHAR(500),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_transfer_line_transfer
        FOREIGN KEY(stock_transfer_id)
        REFERENCES stock_transfers(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_transfer_line_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
);

CREATE INDEX idx_transfer_line_transfer
ON stock_transfer_lines(stock_transfer_id);

CREATE INDEX idx_transfer_line_product
ON stock_transfer_lines(product_id);

-- ==========================================================
-- STOCK COUNTS
-- ==========================================================

CREATE TABLE stock_counts
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    count_number                VARCHAR(50) NOT NULL UNIQUE,

    warehouse_id                UUID NOT NULL,

    counted_by                  UUID,

    approved_by                 UUID,

    status                      VARCHAR(30) NOT NULL,

    remarks                     TEXT,

    count_date                  TIMESTAMP NOT NULL,

    approved_at                 TIMESTAMP,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                      BOOLEAN NOT NULL DEFAULT TRUE,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_stock_count_wh
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_stock_count_user
        FOREIGN KEY(counted_by)
        REFERENCES users(id),

    CONSTRAINT fk_stock_count_approved
        FOREIGN KEY(approved_by)
        REFERENCES users(id)
);

CREATE INDEX idx_stock_count_number
ON stock_counts(count_number);

CREATE INDEX idx_stock_count_status
ON stock_counts(status);

CREATE INDEX idx_stock_count_wh
ON stock_counts(warehouse_id);

-- ==========================================================
-- STOCK COUNT LINES
-- ==========================================================

CREATE TABLE stock_count_lines
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    stock_count_id          UUID NOT NULL,

    product_id              UUID NOT NULL,

    system_quantity         NUMERIC(18,2) NOT NULL,

    counted_quantity        NUMERIC(18,2) NOT NULL,

    variance_quantity       NUMERIC(18,2) NOT NULL,

    remarks                 VARCHAR(500),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_count_line_count
        FOREIGN KEY(stock_count_id)
        REFERENCES stock_counts(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_count_line_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
);

CREATE INDEX idx_count_line_count
ON stock_count_lines(stock_count_id);

CREATE INDEX idx_count_line_product
ON stock_count_lines(product_id);

-- ==========================================================
-- STOCK ADJUSTMENTS
-- ==========================================================

CREATE TABLE stock_adjustments
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    adjustment_number           VARCHAR(50) NOT NULL UNIQUE,

    warehouse_id                UUID NOT NULL,

    approved_by                 UUID,

    adjustment_reason           VARCHAR(255),

    status                      VARCHAR(30) NOT NULL,

    adjustment_date             TIMESTAMP NOT NULL,

    remarks                     TEXT,

    approved_at                 TIMESTAMP,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                      BOOLEAN NOT NULL DEFAULT TRUE,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_adjustment_wh
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_adjustment_user
        FOREIGN KEY(approved_by)
        REFERENCES users(id)
);

CREATE INDEX idx_adjustment_number
ON stock_adjustments(adjustment_number);

CREATE INDEX idx_adjustment_status
ON stock_adjustments(status);

CREATE INDEX idx_adjustment_wh
ON stock_adjustments(warehouse_id);

-- ==========================================================
-- STOCK ADJUSTMENT LINES
-- ==========================================================

CREATE TABLE stock_adjustment_lines
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    stock_adjustment_id         UUID NOT NULL,

    product_id                  UUID NOT NULL,

    quantity_before             NUMERIC(18,2) NOT NULL,

    quantity_after              NUMERIC(18,2) NOT NULL,

    adjustment_quantity         NUMERIC(18,2) NOT NULL,

    unit_cost                   NUMERIC(18,2),

    remarks                     VARCHAR(500),

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                      BOOLEAN NOT NULL DEFAULT TRUE,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_adjustment_line_adjustment
        FOREIGN KEY(stock_adjustment_id)
        REFERENCES stock_adjustments(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_adjustment_line_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
);

CREATE INDEX idx_adjustment_line_adjustment
ON stock_adjustment_lines(stock_adjustment_id);

CREATE INDEX idx_adjustment_line_product
ON stock_adjustment_lines(product_id);

-- ==========================================================
-- PART 6
-- SALES & INVOICING
-- ==========================================================

-- ==========================================================
-- CUSTOMERS
-- ==========================================================

CREATE TABLE customers
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    customer_code           VARCHAR(30) NOT NULL UNIQUE,

    customer_name           VARCHAR(255) NOT NULL,

    contact_person          VARCHAR(150),

    phone                   VARCHAR(50),

    email                   VARCHAR(150),

    tax_number              VARCHAR(100),

    address                 VARCHAR(500),

    city                    VARCHAR(100),

    country                 VARCHAR(100),

    credit_limit            NUMERIC(18,2) DEFAULT 0,

    payment_terms           INTEGER DEFAULT 30,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_customer_code
ON customers(customer_code);

CREATE INDEX idx_customer_name
ON customers(customer_name);

CREATE INDEX idx_customer_email
ON customers(email);

-- ==========================================================
-- SALES INVOICES
-- ==========================================================

CREATE TABLE invoices
(
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    invoice_number              VARCHAR(50) NOT NULL UNIQUE,

    customer_id                 UUID NOT NULL,

    warehouse_id                UUID NOT NULL,

    created_by                  UUID,

    approved_by                 UUID,

    cancelled_by                UUID,

    status                      VARCHAR(30) NOT NULL,

    subtotal                    NUMERIC(18,2) NOT NULL DEFAULT 0,

    tax_amount                  NUMERIC(18,2) NOT NULL DEFAULT 0,

    discount_amount             NUMERIC(18,2) NOT NULL DEFAULT 0,

    total_amount                NUMERIC(18,2) NOT NULL DEFAULT 0,

    invoice_date                TIMESTAMP NOT NULL,

    due_date                    TIMESTAMP,

    approved_at                 TIMESTAMP,

    cancelled_at                TIMESTAMP,

    remarks                     TEXT,

    created_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                      BOOLEAN NOT NULL DEFAULT TRUE,

    version                     BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_invoice_customer
        FOREIGN KEY(customer_id)
        REFERENCES customers(id),

    CONSTRAINT fk_invoice_warehouse
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_invoice_created_by
        FOREIGN KEY(created_by)
        REFERENCES users(id),

    CONSTRAINT fk_invoice_approved_by
        FOREIGN KEY(approved_by)
        REFERENCES users(id),

    CONSTRAINT fk_invoice_cancelled_by
        FOREIGN KEY(cancelled_by)
        REFERENCES users(id)
);

CREATE INDEX idx_invoice_number
ON invoices(invoice_number);

CREATE INDEX idx_invoice_customer
ON invoices(customer_id);

CREATE INDEX idx_invoice_status
ON invoices(status);

CREATE INDEX idx_invoice_date
ON invoices(invoice_date);

CREATE INDEX idx_invoice_warehouse
ON invoices(warehouse_id);

-- ==========================================================
-- INVOICE LINES
-- ==========================================================

CREATE TABLE invoice_lines
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    invoice_id              UUID NOT NULL,

    product_id              UUID NOT NULL,

    quantity                NUMERIC(18,2) NOT NULL,

    unit_price              NUMERIC(18,2) NOT NULL,

    discount_percent        NUMERIC(8,2) DEFAULT 0,

    tax_percent             NUMERIC(8,2) DEFAULT 0,

    line_total              NUMERIC(18,2) NOT NULL,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_invoice_line_invoice
        FOREIGN KEY(invoice_id)
        REFERENCES invoices(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_invoice_line_product
        FOREIGN KEY(product_id)
        REFERENCES products(id)
);

CREATE INDEX idx_invoice_line_invoice
ON invoice_lines(invoice_id);

CREATE INDEX idx_invoice_line_product
ON invoice_lines(product_id);

-- ==========================================================
-- CUSTOMER PAYMENTS
-- ==========================================================

CREATE TABLE customer_payments
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    payment_number          VARCHAR(50) NOT NULL UNIQUE,

    invoice_id              UUID NOT NULL,

    customer_id             UUID NOT NULL,

    payment_method          VARCHAR(30),

    amount_paid             NUMERIC(18,2) NOT NULL,

    payment_date            TIMESTAMP NOT NULL,

    reference_number        VARCHAR(100),

    remarks                 TEXT,

    created_by              UUID,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_payment_invoice
        FOREIGN KEY(invoice_id)
        REFERENCES invoices(id),

    CONSTRAINT fk_payment_customer
        FOREIGN KEY(customer_id)
        REFERENCES customers(id),

    CONSTRAINT fk_payment_user
        FOREIGN KEY(created_by)
        REFERENCES users(id)
);

CREATE INDEX idx_payment_invoice
ON customer_payments(invoice_id);

CREATE INDEX idx_payment_customer
ON customer_payments(customer_id);

CREATE INDEX idx_payment_date
ON customer_payments(payment_date);

-- ==========================================================
-- PART 7
-- SYSTEM ADMINISTRATION
-- ==========================================================

-- ==========================================================
-- SYSTEM SETTINGS
-- ==========================================================

CREATE TABLE system_settings
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    setting_key             VARCHAR(150) NOT NULL UNIQUE,

    setting_value           TEXT,

    description             VARCHAR(500),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_system_settings_key
ON system_settings(setting_key);


-- ==========================================================
-- AUDIT LOGS
-- ==========================================================

CREATE TABLE audit_logs
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    username                VARCHAR(150),

    module                  VARCHAR(100),

    entity_name             VARCHAR(150),

    entity_id               UUID,

    action                  VARCHAR(50),

    old_values              JSONB,

    new_values              JSONB,

    ip_address              VARCHAR(100),

    user_agent              TEXT,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_module
ON audit_logs(module);

CREATE INDEX idx_audit_entity
ON audit_logs(entity_name);

CREATE INDEX idx_audit_user
ON audit_logs(username);

CREATE INDEX idx_audit_date
ON audit_logs(created_at);

-- ==========================================================
-- ATTACHMENTS
-- ==========================================================

CREATE TABLE attachments
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    module                  VARCHAR(100),

    entity_id               UUID NOT NULL,

    file_name               VARCHAR(255) NOT NULL,

    file_type               VARCHAR(100),

    file_size               BIGINT,

    storage_path            VARCHAR(500),

    uploaded_by             UUID,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_attachment_user
        FOREIGN KEY(uploaded_by)
        REFERENCES users(id)
);

CREATE INDEX idx_attachment_entity
ON attachments(module, entity_id);

-- ==========================================================
-- APPROVAL WORKFLOWS
-- ==========================================================

CREATE TABLE approval_workflows
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    module                  VARCHAR(100) NOT NULL,

    minimum_amount          NUMERIC(18,2),

    maximum_amount          NUMERIC(18,2),

    approval_level          INTEGER NOT NULL,

    role_id                 UUID NOT NULL,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_workflow_role
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
);

-- ==========================================================
-- APPROVAL HISTORY
-- ==========================================================

CREATE TABLE approval_history
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    module                  VARCHAR(100),

    entity_id               UUID,

    approval_level          INTEGER,

    approved_by             UUID,

    action                  VARCHAR(50),

    remarks                 TEXT,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_history_user
        FOREIGN KEY(approved_by)
        REFERENCES users(id)
);

CREATE INDEX idx_approval_history
ON approval_history(module, entity_id);

-- ==========================================================
-- AI PROCUREMENT REQUESTS
-- ==========================================================

CREATE TABLE ai_procurement_requests
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    warehouse_id            UUID,

    product_id              UUID,

    current_stock           NUMERIC(18,2),

    average_consumption     NUMERIC(18,2),

    suggested_quantity      NUMERIC(18,2),

    confidence_score        NUMERIC(5,2),

    recommendation          TEXT,

    recommendation_status   VARCHAR(30),

    created_by              UUID,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ai_warehouse
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_ai_product
        FOREIGN KEY(product_id)
        REFERENCES products(id),

    CONSTRAINT fk_ai_user
        FOREIGN KEY(created_by)
        REFERENCES users(id)
);

CREATE INDEX idx_ai_product
ON ai_procurement_requests(product_id);

CREATE INDEX idx_ai_warehouse
ON ai_procurement_requests(warehouse_id);

-- ==========================================================
-- NOTIFICATIONS
-- ==========================================================

CREATE TABLE notifications
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id                 UUID NOT NULL,

    title                   VARCHAR(255),

    message                 TEXT,

    notification_type       VARCHAR(50),

    read_flag               BOOLEAN DEFAULT FALSE,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_notification_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);

CREATE INDEX idx_notification_user
ON notifications(user_id);

CREATE INDEX idx_notification_read
ON notifications(read_flag);

-- ==========================================================
-- SCHEDULED JOBS
-- ==========================================================

CREATE TABLE scheduled_jobs
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    job_name                VARCHAR(150) NOT NULL UNIQUE,

    cron_expression         VARCHAR(100),

    enabled                 BOOLEAN DEFAULT TRUE,

    last_run                TIMESTAMP,

    next_run                TIMESTAMP,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================================
-- PART 8
-- REPORTING
-- ==========================================================

-- ==========================================================
-- SAVED REPORTS
-- ==========================================================

CREATE TABLE reports
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    report_code             VARCHAR(100) NOT NULL UNIQUE,

    report_name             VARCHAR(255) NOT NULL,

    module                  VARCHAR(100),

    sql_query               TEXT,

    created_by              UUID,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_report_user
        FOREIGN KEY(created_by)
        REFERENCES users(id)
);

CREATE INDEX idx_report_module
ON reports(module);

-- ==========================================================
-- REPORT SCHEDULES
-- ==========================================================

CREATE TABLE report_schedules
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    report_id               UUID NOT NULL,

    cron_expression         VARCHAR(100),

    email_to                VARCHAR(500),

    enabled                 BOOLEAN DEFAULT TRUE,

    last_run                TIMESTAMP,

    next_run                TIMESTAMP,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_schedule_report
        FOREIGN KEY(report_id)
        REFERENCES reports(id)
);

CREATE INDEX idx_schedule_report
ON report_schedules(report_id);

-- ==========================================================
-- DASHBOARDS
-- ==========================================================

CREATE TABLE dashboards
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    dashboard_name          VARCHAR(150) NOT NULL,

    created_by              UUID,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN DEFAULT TRUE,

    version                 BIGINT DEFAULT 0,

    CONSTRAINT fk_dashboard_user
        FOREIGN KEY(created_by)
        REFERENCES users(id)
);

CREATE TABLE dashboard_widgets
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    dashboard_id            UUID NOT NULL,

    widget_name             VARCHAR(150),

    widget_type             VARCHAR(100),

    widget_configuration    JSONB,

    position_x              INTEGER,

    position_y              INTEGER,

    width                   INTEGER,

    height                  INTEGER,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_widget_dashboard
        FOREIGN KEY(dashboard_id)
        REFERENCES dashboards(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_dashboard_widget
ON dashboard_widgets(dashboard_id);

-- ==========================================================
-- EMAIL QUEUE
-- ==========================================================

CREATE TABLE email_queue
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    recipient               VARCHAR(255),

    subject                 VARCHAR(255),

    body                    TEXT,

    attachment_path         VARCHAR(500),

    status                  VARCHAR(30),

    retry_count             INTEGER DEFAULT 0,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    sent_at                 TIMESTAMP
);

CREATE INDEX idx_email_status
ON email_queue(status);

-- ==========================================================
-- SMS QUEUE
-- ==========================================================

CREATE TABLE sms_queue
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    recipient               VARCHAR(50),

    message                 TEXT,

    status                  VARCHAR(30),

    retry_count             INTEGER DEFAULT 0,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    sent_at                 TIMESTAMP
);

CREATE INDEX idx_sms_status
ON sms_queue(status);

-- ==========================================================
-- API KEYS
-- ==========================================================

CREATE TABLE api_keys
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    api_name                VARCHAR(150),

    api_key                 TEXT,

    api_secret              TEXT,

    base_url                VARCHAR(500),

    active                  BOOLEAN DEFAULT TRUE,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================================
-- AI CHAT HISTORY
-- ==========================================================

CREATE TABLE ai_chat_history
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    user_id                 UUID,

    prompt                  TEXT,

    response                TEXT,

    model                   VARCHAR(100),

    tokens_used             INTEGER,

    response_time_ms        BIGINT,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ai_chat_user
        FOREIGN KEY(user_id)
        REFERENCES users(id)
);

CREATE INDEX idx_ai_chat_user
ON ai_chat_history(user_id);

-- ==========================================================
-- OCR DOCUMENTS
-- ==========================================================

CREATE TABLE ocr_documents
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    module                  VARCHAR(100),

    entity_id               UUID,

    original_file           VARCHAR(500),

    extracted_text          TEXT,

    processed               BOOLEAN DEFAULT FALSE,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================================
-- MACHINE LEARNING FORECASTS
-- ==========================================================

CREATE TABLE demand_forecasts
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    warehouse_id            UUID,

    product_id              UUID,

    forecast_month          DATE,

    predicted_quantity      NUMERIC(18,2),

    confidence              NUMERIC(5,2),

    generated_at            TIMESTAMP,

    CONSTRAINT fk_forecast_product
        FOREIGN KEY(product_id)
        REFERENCES products(id),

    CONSTRAINT fk_forecast_warehouse
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id)
);

CREATE INDEX idx_forecast_product
ON demand_forecasts(product_id);

CREATE INDEX idx_forecast_month
ON demand_forecasts(forecast_month);


-- ==========================================================
-- PART 9
-- FINANCE FOUNDATION
-- ==========================================================

-- ==========================================================
-- CURRENCIES
-- ==========================================================

CREATE TABLE currencies
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    currency_code           VARCHAR(10) NOT NULL UNIQUE,

    currency_name           VARCHAR(100) NOT NULL,

    currency_symbol         VARCHAR(10),

    decimal_places          INTEGER DEFAULT 2,

    is_base_currency        BOOLEAN DEFAULT FALSE,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_currency_code
ON currencies(currency_code);

-- ==========================================================
-- EXCHANGE RATES
-- ==========================================================

CREATE TABLE exchange_rates
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    currency_id             UUID NOT NULL,

    exchange_rate           NUMERIC(18,8) NOT NULL,

    effective_date          DATE NOT NULL,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_exchange_currency
        FOREIGN KEY(currency_id)
        REFERENCES currencies(id)
);

CREATE INDEX idx_exchange_currency
ON exchange_rates(currency_id);

CREATE INDEX idx_exchange_date
ON exchange_rates(effective_date);

-- ==========================================================
-- TAX CODES
-- ==========================================================

CREATE TABLE tax_codes
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    tax_code                VARCHAR(30) NOT NULL UNIQUE,

    tax_name                VARCHAR(150) NOT NULL,

    tax_rate                NUMERIC(8,2) NOT NULL,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_tax_code
ON tax_codes(tax_code);

-- ==========================================================
-- COST CENTRES
-- ==========================================================

CREATE TABLE cost_centres
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    code                    VARCHAR(30) NOT NULL UNIQUE,

    name                    VARCHAR(255) NOT NULL,

    description             VARCHAR(500),

    manager                 VARCHAR(150),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_costcentre_code
ON cost_centres(code);

-- ==========================================================
-- GENERAL LEDGER ACCOUNTS
-- ==========================================================

CREATE TABLE gl_accounts
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    account_code            VARCHAR(30) NOT NULL UNIQUE,

    account_name            VARCHAR(255) NOT NULL,

    account_type            VARCHAR(50) NOT NULL,

    parent_account_id       UUID,

    posting_allowed         BOOLEAN DEFAULT TRUE,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_gl_parent
        FOREIGN KEY(parent_account_id)
        REFERENCES gl_accounts(id)
);

CREATE INDEX idx_gl_code
ON gl_accounts(account_code);

CREATE INDEX idx_gl_type
ON gl_accounts(account_type);

-- ==========================================================
-- FISCAL YEARS
-- ==========================================================

CREATE TABLE fiscal_years
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    financial_year          INTEGER NOT NULL UNIQUE,

    start_date              DATE NOT NULL,

    end_date                DATE NOT NULL,

    closed                  BOOLEAN DEFAULT FALSE,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================================
-- ACCOUNTING PERIODS
-- ==========================================================

CREATE TABLE accounting_periods
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    fiscal_year_id          UUID NOT NULL,

    period_number           INTEGER NOT NULL,
    period_name             VARCHAR(50) NOT NULL,

    start_date              DATE NOT NULL,

    end_date                DATE NOT NULL,

    closed                  BOOLEAN DEFAULT FALSE,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_accounting_period_year
        FOREIGN KEY (fiscal_year_id)
        REFERENCES fiscal_years(id),

    CONSTRAINT uq_accounting_period
        UNIQUE(fiscal_year_id, period_number)
);

CREATE INDEX idx_accounting_period_year
ON accounting_periods(fiscal_year_id);

CREATE INDEX idx_accounting_period_closed
ON accounting_periods(closed);

-- ==========================================================
-- JOURNAL ENTRIES
-- ==========================================================

CREATE TABLE journal_entries
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    journal_number          VARCHAR(50) NOT NULL UNIQUE,

    transaction_date        TIMESTAMP NOT NULL,

    reference_number        VARCHAR(100),

    narration               TEXT,

    status                  VARCHAR(30) NOT NULL,

    created_by              UUID,

    approved_by             UUID,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_journal_created_by
        FOREIGN KEY(created_by)
        REFERENCES users(id),

    CONSTRAINT fk_journal_approved_by
        FOREIGN KEY(approved_by)
        REFERENCES users(id)
);

CREATE INDEX idx_journal_number
ON journal_entries(journal_number);

CREATE INDEX idx_journal_date
ON journal_entries(transaction_date);

-- ==========================================================
-- JOURNAL ENTRY LINES
-- ==========================================================

CREATE TABLE journal_entry_lines
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    journal_entry_id        UUID NOT NULL,

    gl_account_id           UUID NOT NULL,

    cost_centre_id          UUID,

    debit_amount            NUMERIC(18,2) DEFAULT 0,

    credit_amount           NUMERIC(18,2) DEFAULT 0,

    description             VARCHAR(500),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_journal_line_entry
        FOREIGN KEY(journal_entry_id)
        REFERENCES journal_entries(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_journal_line_account
        FOREIGN KEY(gl_account_id)
        REFERENCES gl_accounts(id),

    CONSTRAINT fk_journal_line_costcentre
        FOREIGN KEY(cost_centre_id)
        REFERENCES cost_centres(id)
);

CREATE INDEX idx_journal_line_entry
ON journal_entry_lines(journal_entry_id);

CREATE INDEX idx_journal_line_account
ON journal_entry_lines(gl_account_id);

-- ==========================================================
-- BUDGETS
-- ==========================================================

CREATE TABLE budgets
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    budget_code             VARCHAR(30) NOT NULL UNIQUE,

    budget_name             VARCHAR(255) NOT NULL,

    fiscal_year_id          UUID NOT NULL,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_budget_year
        FOREIGN KEY(fiscal_year_id)
        REFERENCES fiscal_years(id)
);

CREATE INDEX idx_budget_year
ON budgets(fiscal_year_id);


-- ==========================================================
-- BUDGET LINES
-- ==========================================================

CREATE TABLE budget_lines
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    budget_id               UUID NOT NULL,

    gl_account_id           UUID NOT NULL,

    cost_centre_id          UUID,

    annual_amount           NUMERIC(18,2) NOT NULL,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_budget_line_budget
        FOREIGN KEY(budget_id)
        REFERENCES budgets(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_budget_line_account
        FOREIGN KEY(gl_account_id)
        REFERENCES gl_accounts(id),

    CONSTRAINT fk_budget_line_costcentre
        FOREIGN KEY(cost_centre_id)
        REFERENCES cost_centres(id)
);

CREATE INDEX idx_budget_line_budget
ON budget_lines(budget_id);

CREATE INDEX idx_budget_line_account
ON budget_lines(gl_account_id);

-- ==========================================================
-- INVENTORY VALUATION
-- ==========================================================

CREATE TABLE inventory_valuation
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    warehouse_id            UUID NOT NULL,

    product_id              UUID NOT NULL,

    valuation_date          DATE NOT NULL,

    quantity                NUMERIC(18,2) NOT NULL,

    unit_cost               NUMERIC(18,2) NOT NULL,

    total_value             NUMERIC(18,2) NOT NULL,

    valuation_method        VARCHAR(30) NOT NULL,

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_inventory_valuation_product
        FOREIGN KEY(product_id)
        REFERENCES products(id),

    CONSTRAINT fk_inventory_valuation_warehouse
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id)
);

CREATE INDEX idx_inventory_valuation_product
ON inventory_valuation(product_id);

CREATE INDEX idx_inventory_valuation_date
ON inventory_valuation(valuation_date);

-- ==========================================================
-- PROCUREMENT ACCRUALS
-- ==========================================================

CREATE TABLE procurement_accruals
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    purchase_order_id        UUID NOT NULL,

    goods_receipt_id         UUID,

    invoice_id               UUID,

    accrued_amount           NUMERIC(18,2) NOT NULL,

    status                   VARCHAR(30),

    created_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_accrual_po
        FOREIGN KEY(purchase_order_id)
        REFERENCES purchase_orders(id),

    CONSTRAINT fk_accrual_grn
        FOREIGN KEY(goods_receipt_id)
        REFERENCES goods_receipts(id),

    CONSTRAINT fk_accrual_invoice
        FOREIGN KEY(invoice_id)
        REFERENCES invoices(id)
);

CREATE INDEX idx_accrual_po
ON procurement_accruals(purchase_order_id);

-- ==========================================================
-- PART 10
-- BUSINESS INTELLIGENCE & AUTOMATION
-- ==========================================================

-- ==========================================================
-- KPI DEFINITIONS
-- ==========================================================

CREATE TABLE kpi_definitions
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    kpi_code                VARCHAR(100) NOT NULL UNIQUE,

    kpi_name                VARCHAR(255) NOT NULL,

    module                  VARCHAR(100),

    calculation_sql         TEXT,

    refresh_interval        VARCHAR(50),

    description             VARCHAR(500),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_kpi_module
ON kpi_definitions(module);

-- ==========================================================
-- KPI VALUES
-- ==========================================================

CREATE TABLE kpi_values
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    kpi_id                  UUID NOT NULL,

    warehouse_id            UUID,

    period_date             DATE,

    kpi_value               NUMERIC(18,4),

    calculated_at           TIMESTAMP,

    CONSTRAINT fk_kpi_value_definition
        FOREIGN KEY(kpi_id)
        REFERENCES kpi_definitions(id),

    CONSTRAINT fk_kpi_value_warehouse
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id)
);

CREATE INDEX idx_kpi_values_period
ON kpi_values(period_date);

-- ==========================================================
-- WORKFLOW DEFINITIONS
-- ==========================================================

CREATE TABLE workflow_definitions
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    workflow_code           VARCHAR(100) UNIQUE,

    workflow_name           VARCHAR(255),

    module                  VARCHAR(100),

    description             TEXT,

    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN DEFAULT TRUE,

    version                 BIGINT DEFAULT 0
);

CREATE TABLE workflow_steps
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    workflow_id             UUID NOT NULL,

    step_number             INTEGER NOT NULL,

    role_id                 UUID NOT NULL,

    action_name             VARCHAR(100),

    next_step               INTEGER,

    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_workflow_step
        FOREIGN KEY(workflow_id)
        REFERENCES workflow_definitions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_workflow_role
        FOREIGN KEY(role_id)
        REFERENCES roles(id)
);

CREATE INDEX idx_workflow_steps
ON workflow_steps(workflow_id);


-- ==========================================================
-- USER TASKS
-- ==========================================================

CREATE TABLE workflow_tasks
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    workflow_id             UUID,

    entity_type             VARCHAR(100),

    entity_id               UUID,

    assigned_to             UUID,

    status                  VARCHAR(30),

    priority                VARCHAR(20),

    due_date                TIMESTAMP,

    completed_at            TIMESTAMP,

    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_task_user
        FOREIGN KEY(assigned_to)
        REFERENCES users(id),

    CONSTRAINT fk_task_workflow
        FOREIGN KEY(workflow_id)
        REFERENCES workflow_definitions(id)
);

CREATE INDEX idx_task_user
ON workflow_tasks(assigned_to);

CREATE INDEX idx_task_status
ON workflow_tasks(status);

-- ==========================================================
-- WEBHOOKS
-- ==========================================================

CREATE TABLE webhooks
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    webhook_name            VARCHAR(255),

    event_name              VARCHAR(255),

    endpoint_url            TEXT,

    secret_key              VARCHAR(255),

    enabled                 BOOLEAN DEFAULT TRUE,

    last_success            TIMESTAMP,

    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- ==========================================================
-- INTEGRATION LOG
-- ==========================================================

CREATE TABLE integration_logs
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    integration_name        VARCHAR(255),

    direction               VARCHAR(30),

    payload                 JSONB,

    response                JSONB,

    status                  VARCHAR(30),

    created_at              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_integration_status
ON integration_logs(status);

-- ==========================================================
-- IMPORT JOBS
-- ==========================================================

CREATE TABLE import_jobs
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    job_name                VARCHAR(255),

    file_name               VARCHAR(255),

    module                  VARCHAR(100),

    total_records           INTEGER,

    successful_records      INTEGER,

    failed_records          INTEGER,

    status                  VARCHAR(30),

    started_at              TIMESTAMP,

    completed_at            TIMESTAMP,

    created_by              UUID,

    CONSTRAINT fk_import_user
        FOREIGN KEY(created_by)
        REFERENCES users(id)
);

CREATE INDEX idx_import_status
ON import_jobs(status);

-- ==========================================================
-- EXPORT JOBS
-- ==========================================================

CREATE TABLE export_jobs
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    job_name                VARCHAR(255),

    module                  VARCHAR(100),

    format                  VARCHAR(30),

    exported_file           VARCHAR(500),

    status                  VARCHAR(30),

    started_at              TIMESTAMP,

    completed_at            TIMESTAMP,

    created_by              UUID,

    CONSTRAINT fk_export_user
        FOREIGN KEY(created_by)
        REFERENCES users(id)
);

CREATE INDEX idx_export_status
ON export_jobs(status);


-- ==========================================================
-- DASHBOARD CACHE
-- ==========================================================

CREATE TABLE dashboard_cache
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    widget_name             VARCHAR(150),

    warehouse_id            UUID,

    cache_json              JSONB,

    generated_at            TIMESTAMP,

    expires_at              TIMESTAMP,

    CONSTRAINT fk_dashboard_cache_wh
        FOREIGN KEY(warehouse_id)
        REFERENCES warehouses(id)
);

CREATE INDEX idx_dashboard_cache_expiry
ON dashboard_cache(expires_at);

CREATE TABLE notification_templates
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    template_code       VARCHAR(100) NOT NULL UNIQUE,

    template_name       VARCHAR(255) NOT NULL,

    subject             VARCHAR(255),

    message             TEXT NOT NULL,

    notification_type   VARCHAR(30) NOT NULL,

    enabled             BOOLEAN DEFAULT TRUE,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active              BOOLEAN NOT NULL DEFAULT TRUE,

    version             BIGINT NOT NULL DEFAULT 0
);

CREATE INDEX idx_notification_template_code
ON notification_templates(template_code);