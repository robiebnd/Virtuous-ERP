-- ==========================================================
-- V11__create_bin_and_inventory_bin_tables.sql
-- BIN / INVENTORY BIN FOUNDATION
-- ==========================================================
--
-- V13 and V14 add bin references to inventory_transactions.
-- These tables therefore must exist before those migrations run.
--
-- Tables created:
--   bins
--   inventory_bins
--
-- Both tables follow BaseEntity conventions used by the Java model:
-- id, created_at, updated_at, active, version.
-- ==========================================================

-- ==========================================================
-- BINS
-- ==========================================================

CREATE TABLE bins
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    warehouse_id        UUID NOT NULL,

    code                VARCHAR(30) NOT NULL,

    name                VARCHAR(100) NOT NULL,

    type                VARCHAR(50) NOT NULL,

    receiving_bin       BOOLEAN NOT NULL DEFAULT FALSE,

    capacity            NUMERIC(18,2) DEFAULT 0,

    active              BOOLEAN NOT NULL DEFAULT TRUE,

    status              VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',

    barcode             VARCHAR(255),

    sequence            INTEGER,

    used_capacity       NUMERIC(18,2) NOT NULL DEFAULT 0,

    description         VARCHAR(500),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_bin_code
        UNIQUE (warehouse_id, code),

    CONSTRAINT uk_bin_barcode
        UNIQUE (barcode),

    CONSTRAINT fk_bin_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT chk_bin_capacity
        CHECK (capacity IS NULL OR capacity >= 0),

    CONSTRAINT chk_bin_used_capacity
        CHECK (used_capacity >= 0)
);

CREATE INDEX idx_bins_warehouse
ON bins(warehouse_id);

CREATE INDEX idx_bins_code
ON bins(code);

CREATE INDEX idx_bins_status
ON bins(status);

CREATE INDEX idx_bins_type
ON bins(type);

CREATE INDEX idx_bins_receiving
ON bins(receiving_bin);

CREATE INDEX idx_bins_active
ON bins(active);

-- ==========================================================
-- INVENTORY BINS
-- ==========================================================

CREATE TABLE inventory_bins
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    warehouse_id        UUID NOT NULL,

    bin_id              UUID NOT NULL,

    product_id          UUID NOT NULL,

    quantity_on_hand    NUMERIC(18,2) NOT NULL DEFAULT 0,

    quantity_reserved   NUMERIC(18,2) NOT NULL DEFAULT 0,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active              BOOLEAN NOT NULL DEFAULT TRUE,

    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_inventory_bin
        UNIQUE (warehouse_id, bin_id, product_id),

    CONSTRAINT fk_inventory_bin_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_inventory_bin_bin
        FOREIGN KEY (bin_id)
        REFERENCES bins(id),

    CONSTRAINT fk_inventory_bin_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),

    CONSTRAINT chk_inventory_bin_quantity_on_hand
        CHECK (quantity_on_hand >= 0),

    CONSTRAINT chk_inventory_bin_quantity_reserved
        CHECK (quantity_reserved >= 0)
);

CREATE INDEX idx_inventory_bins_warehouse
ON inventory_bins(warehouse_id);

CREATE INDEX idx_inventory_bins_bin
ON inventory_bins(bin_id);

CREATE INDEX idx_inventory_bins_product
ON inventory_bins(product_id);

CREATE INDEX idx_inventory_bins_active
ON inventory_bins(active);

-- ==========================================================
-- END V11
-- ==========================================================
