-- ==========================================================
-- V18__create_goods_movement_tables.sql
-- GOODS MOVEMENT
-- ==========================================================
--
-- Goods Movement is the transactional layer used to record
-- warehouse stock movements before they are posted to inventory.
--
-- The Java entities require:
--   goods_movements
--   goods_movement_lines
--
-- BaseEntity fields are included in both tables.
-- ==========================================================

-- ==========================================================
-- GOODS MOVEMENTS
-- ==========================================================

CREATE TABLE goods_movements
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    movement_number     VARCHAR(50) NOT NULL UNIQUE,

    movement_type       VARCHAR(50) NOT NULL,

    status              VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    warehouse_id        UUID NOT NULL,

    reference_number    VARCHAR(50) NOT NULL,

    reference_type      VARCHAR(50) NOT NULL,

    performed_by        UUID,

    movement_date       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    remarks             VARCHAR(1000),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active              BOOLEAN NOT NULL DEFAULT TRUE,

    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_goods_movement_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_goods_movement_performed_by
        FOREIGN KEY (performed_by)
        REFERENCES users(id)
);

CREATE INDEX idx_goods_movement_warehouse
ON goods_movements(warehouse_id);

CREATE INDEX idx_goods_movement_type
ON goods_movements(movement_type);

CREATE INDEX idx_goods_movement_status
ON goods_movements(status);

CREATE INDEX idx_goods_movement_reference
ON goods_movements(reference_type, reference_number);

CREATE INDEX idx_goods_movement_performed_by
ON goods_movements(performed_by);

CREATE INDEX idx_goods_movement_date
ON goods_movements(movement_date);

-- ==========================================================
-- GOODS MOVEMENT LINES
-- ==========================================================

CREATE TABLE goods_movement_lines
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    goods_movement_id   UUID NOT NULL,

    product_id          UUID NOT NULL,

    from_bin_id         UUID,

    to_bin_id           UUID,

    quantity            NUMERIC(18,2) NOT NULL,

    unit_cost            NUMERIC(18,2),

    remarks             VARCHAR(500),

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active              BOOLEAN NOT NULL DEFAULT TRUE,

    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_goods_movement_line_movement
        FOREIGN KEY (goods_movement_id)
        REFERENCES goods_movements(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_goods_movement_line_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),

    CONSTRAINT fk_goods_movement_line_from_bin
        FOREIGN KEY (from_bin_id)
        REFERENCES bins(id),

    CONSTRAINT fk_goods_movement_line_to_bin
        FOREIGN KEY (to_bin_id)
        REFERENCES bins(id),

    CONSTRAINT chk_goods_movement_line_quantity
        CHECK (quantity > 0)
);

CREATE INDEX idx_goods_movement_line_movement
ON goods_movement_lines(goods_movement_id);

CREATE INDEX idx_goods_movement_line_product
ON goods_movement_lines(product_id);

CREATE INDEX idx_goods_movement_line_from_bin
ON goods_movement_lines(from_bin_id);

CREATE INDEX idx_goods_movement_line_to_bin
ON goods_movement_lines(to_bin_id);

-- ==========================================================
-- END V18
-- ==========================================================
