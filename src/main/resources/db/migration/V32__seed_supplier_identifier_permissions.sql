-- ==========================================================
-- V32__seed_supplier_identifier_permissions.sql
-- SUPPLIER PRODUCT IDENTIFIER PERMISSIONS
-- ==========================================================

INSERT INTO permissions
(
    code,
    name,
    description
)
VALUES
(
    'PRODUCT_SUPPLIER_IDENTIFIER_VIEW',
    'View Supplier Product Identifiers',
    'View supplier product identifiers'
),
(
    'PRODUCT_SUPPLIER_IDENTIFIER_CREATE',
    'Create Supplier Product Identifier',
    'Create supplier product identifiers'
),
(
    'PRODUCT_SUPPLIER_IDENTIFIER_UPDATE',
    'Update Supplier Product Identifier',
    'Update supplier product identifiers'
),
(
    'PRODUCT_SUPPLIER_IDENTIFIER_DELETE',
    'Delete Supplier Product Identifier',
    'Delete supplier product identifiers'
)
ON CONFLICT (code) DO NOTHING;

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
    ON p.code IN (
        'PRODUCT_SUPPLIER_IDENTIFIER_VIEW',
        'PRODUCT_SUPPLIER_IDENTIFIER_CREATE',
        'PRODUCT_SUPPLIER_IDENTIFIER_UPDATE',
        'PRODUCT_SUPPLIER_IDENTIFIER_DELETE'
    )
WHERE r.name IN (
    'SYSTEM_ADMIN',
    'PROCUREMENT_MANAGER',
    'PURCHASING_OFFICER'
)
ON CONFLICT DO NOTHING;
