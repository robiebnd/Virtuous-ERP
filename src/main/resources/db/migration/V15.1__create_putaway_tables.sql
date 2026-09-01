-- ==========================================================
-- V15.1__create_putaway_tables.sql
-- PUT AWAY FOUNDATION
-- ==========================================================
--
-- V16 aligns these tables with BaseEntity by adding the version
-- column. The PutAway Java entities map to put_aways and
-- put_away_lines, so both tables must exist before V16 executes.
-- ==========================================================

-- ==========================================================
-- PUT AWAYS
-- ==========================================================

CREATE TABLE put_aways
(
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    put_away_number     VARCHAR(255) NOT NULL UNIQUE,

    goods_receipt_id    UUID NOT NULL,

    warehouse_id        UUID NOT NULL,

    status              VARCHAR(50) NOT NULL DEFAULT 'DRAFT',

    remarks             VARCHAR(1000),

    initiated_by        UUID,

    completed_by        UUID,

    completed_at        TIMESTAMP,

    assigned_to         UUID,

    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active              BOOLEAN NOT NULL DEFAULT TRUE,

    version             BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_put_away_goods_receipt
        FOREIGN KEY (goods_receipt_id)
        REFERENCES goods_receipts(id),

    CONSTRAINT fk_put_away_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_put_away_initiated_by
        FOREIGN KEY (initiated_by)
        REFERENCES users(id),

    CONSTRAINT fk_put_away_completed_by
        FOREIGN KEY (completed_by)
        REFERENCES users(id),

    CONSTRAINT fk_put_away_assigned_to
        FOREIGN KEY (assigned_to)
        REFERENCES users(id)
);

CREATE INDEX idx_put_aways_number
ON put_aways(put_away_number);

CREATE INDEX idx_put_aways_goods_receipt
ON put_aways(goods_receipt_id);

CREATE INDEX idx_put_aways_warehouse
ON put_aways(warehouse_id);

CREATE INDEX idx_put_aways_status
ON put_aways(status);

CREATE INDEX idx_put_aways_initiated_by
ON put_aways(initiated_by);

CREATE INDEX idx_put_aways_assigned_to
ON put_aways(assigned_to);

-- ==========================================================
-- PUT AWAY LINES
-- ==========================================================

CREATE TABLE put_away_lines
(
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    put_away_id             UUID NOT NULL,

    goods_receipt_line_id   UUID NOT NULL,

    product_id              UUID NOT NULL,

    from_bin_id             UUID NOT NULL,

    to_bin_id               UUID,

    planned_quantity        NUMERIC(18,2) NOT NULL,

    completed_quantity      NUMERIC(18,2) NOT NULL DEFAULT 0,

    status                  VARCHAR(50),

    remarks                 VARCHAR(500),

    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    active                  BOOLEAN NOT NULL DEFAULT TRUE,

    version                 BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_put_away_line_put_away
        FOREIGN KEY (put_away_id)
        REFERENCES put_aways(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_put_away_line_goods_receipt_line
        FOREIGN KEY (goods_receipt_line_id)
        REFERENCES goods_receipt_lines(id),

    CONSTRAINT fk_put_away_line_product
        FOREIGN KEY (product_id)
        REFERENCES products(id),

    CONSTRAINT fk_put_away_line_from_bin
        FOREIGN KEY (from_bin_id)
        REFERENCES bins(id),

    CONSTRAINT fk_put_away_line_to_bin
        FOREIGN KEY (to_bin_id)
        REFERENCES bins(id),

    CONSTRAINT chk_put_away_line_planned_quantity
        CHECK (planned_quantity > 0),

    CONSTRAINT chk_put_away_line_completed_quantity
        CHECK (completed_quantity >= 0)
);

CREATE INDEX idx_put_away_lines_put_away
ON put_away_lines(put_away_id);

CREATE INDEX idx_put_away_lines_goods_receipt_line
ON put_away_lines(goods_receipt_line_id);

CREATE INDEX idx_put_away_lines_product
ON put_away_lines(product_id);

CREATE INDEX idx_put_away_lines_from_bin
ON put_away_lines(from_bin_id);

CREATE INDEX idx_put_away_lines_to_bin
ON put_away_lines(to_bin_id);

CREATE INDEX idx_put_away_lines_status
ON put_away_lines(status);

-- ==========================================================
-- END V15.1
-- ==========================================================
