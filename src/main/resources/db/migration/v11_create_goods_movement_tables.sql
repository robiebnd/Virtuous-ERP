-- ============================================================
-- V11 - Create Goods Movement Tables
-- ============================================================
-- Goods Movement records physical stock movements.
--
-- goods_movements
--      |
--      | 1 : many
--      v
-- goods_movement_lines
--
-- References:
--   warehouses
--   users
--   products
--   bins
-- ============================================================


-- ============================================================
-- 1. GOODS MOVEMENTS
-- ============================================================

CREATE TABLE goods_movements (
    id UUID NOT NULL,

    movement_number VARCHAR(50) NOT NULL,

    movement_type VARCHAR(50) NOT NULL,

    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    warehouse_id UUID NOT NULL,

    reference_number VARCHAR(100) NOT NULL,

    reference_type VARCHAR(50) NOT NULL,

    performed_by UUID,

    movement_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    remarks VARCHAR(1000),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP,

    CONSTRAINT pk_goods_movements
        PRIMARY KEY (id),

    CONSTRAINT uk_goods_movement_number
        UNIQUE (movement_number),

    CONSTRAINT fk_goods_movement_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses(id),

    CONSTRAINT fk_goods_movement_performed_by
        FOREIGN KEY (performed_by)
        REFERENCES users(id),

    CONSTRAINT chk_goods_movement_type
        CHECK (
            movement_type IN (
                'GOODS_RECEIPT',
                'PUT_AWAY',
                'BIN_TRANSFER',
                'STOCK_TRANSFER',
                'CUSTOMER_RETURN',
                'PICK',
                'SHIPMENT',
                'SUPPLIER_RETURN',
                'STOCK_ADJUSTMENT',
                'STOCK_COUNT',
                'WRITE_OFF'
            )
        ),

    CONSTRAINT chk_goods_movement_status
        CHECK (
            status IN (
                'DRAFT',
                'POSTED',
                'CANCELLED'
            )
        )
);


-- ============================================================
-- 2. GOODS MOVEMENT LINES
-- ============================================================

CREATE TABLE goods_movement_lines (
    id UUID NOT NULL,

    goods_movement_id UUID NOT NULL,

    product_id UUID NOT NULL,

    from_bin_id UUID,

    to_bin_id UUID,

    quantity NUMERIC(18,2) NOT NULL,

    unit_cost NUMERIC(18,2),

    remarks VARCHAR(500),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP,

    CONSTRAINT pk_goods_movement_lines
        PRIMARY KEY (id),

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
        CHECK (quantity > 0),

    CONSTRAINT chk_goods_movement_line_unit_cost
        CHECK (
            unit_cost IS NULL
            OR unit_cost >= 0
        ),

    CONSTRAINT chk_goods_movement_line_different_bins
        CHECK (
            from_bin_id IS NULL
            OR to_bin_id IS NULL
            OR from_bin_id <> to_bin_id
        )
);


-- ============================================================
-- 3. INDEXES - GOODS MOVEMENTS
-- ============================================================

CREATE INDEX idx_goods_movements_warehouse
    ON goods_movements(warehouse_id);

CREATE INDEX idx_goods_movements_type
    ON goods_movements(movement_type);

CREATE INDEX idx_goods_movements_status
    ON goods_movements(status);

CREATE INDEX idx_goods_movements_reference_number
    ON goods_movements(reference_number);

CREATE INDEX idx_goods_movements_reference_type
    ON goods_movements(reference_type);

CREATE INDEX idx_goods_movements_reference
    ON goods_movements(
        reference_type,
        reference_number
    );

CREATE INDEX idx_goods_movements_performed_by
    ON goods_movements(performed_by);

CREATE INDEX idx_goods_movements_date
    ON goods_movements(movement_date);


-- ============================================================
-- 4. INDEXES - GOODS MOVEMENT LINES
-- ============================================================

CREATE INDEX idx_goods_movement_lines_movement
    ON goods_movement_lines(goods_movement_id);

CREATE INDEX idx_goods_movement_lines_product
    ON goods_movement_lines(product_id);

CREATE INDEX idx_goods_movement_lines_from_bin
    ON goods_movement_lines(from_bin_id);

CREATE INDEX idx_goods_movement_lines_to_bin
    ON goods_movement_lines(to_bin_id);

CREATE INDEX idx_goods_movement_lines_product_from_bin
    ON goods_movement_lines(
        product_id,
        from_bin_id
    );

CREATE INDEX idx_goods_movement_lines_product_to_bin
    ON goods_movement_lines(
        product_id,
        to_bin_id
    );


-- ============================================================
-- 5. COMMENTS
-- ============================================================

COMMENT ON TABLE goods_movements IS
    'Header records for warehouse goods movements.';

COMMENT ON TABLE goods_movement_lines IS
    'Individual product and bin movements belonging to a goods movement.';

COMMENT ON COLUMN goods_movements.movement_number IS
    'Unique Goods Movement document number.';

COMMENT ON COLUMN goods_movements.movement_type IS
    'Type of stock movement.';

COMMENT ON COLUMN goods_movements.status IS
    'Workflow status of the Goods Movement.';

COMMENT ON COLUMN goods_movements.reference_number IS
    'Source document number such as GRN, Put Away, Stock Transfer or Shipment.';

COMMENT ON COLUMN goods_movements.reference_type IS
    'Type of source document associated with the movement.';

COMMENT ON COLUMN goods_movement_lines.from_bin_id IS
    'Source bin. NULL when stock enters the warehouse.';

COMMENT ON COLUMN goods_movement_lines.to_bin_id IS
    'Destination bin. NULL when stock leaves the warehouse.';

COMMENT ON COLUMN goods_movement_lines.quantity IS
    'Quantity moved. Must always be greater than zero.';
