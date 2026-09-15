-- ==========================================================
-- V27__add_purchase_order_receive_permission.sql
-- Add permission required to receive an approved Purchase Order
-- ==========================================================

INSERT INTO permissions
(
    code,
    name,
    description
)
VALUES
(
    'PURCHASE_ORDER_RECEIVE',
    'Receive Purchase Order',
    'Receive purchase orders'
)
ON CONFLICT (code) DO NOTHING;

--------------------------------------------------------------
-- WAREHOUSE MANAGER + WAREHOUSE CLERK
--------------------------------------------------------------

INSERT INTO role_permissions
(
    id,
    role_id,
    permission_id,
    created_at,
    updated_at,
    active,
    version
)
SELECT
    gen_random_uuid(),
    r.id,
    p.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
FROM roles r
JOIN permissions p
    ON p.code = 'PURCHASE_ORDER_RECEIVE'
WHERE r.name IN (
    'WAREHOUSE_MANAGER',
    'WAREHOUSE_CLERK'
)
ON CONFLICT DO NOTHING;
