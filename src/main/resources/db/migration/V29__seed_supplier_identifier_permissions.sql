INSERT INTO permissions (id, name, description)
VALUES
    (gen_random_uuid(), 'PRODUCT_SUPPLIER_IDENTIFIER_VIEW', 'View supplier product identifiers'),
    (gen_random_uuid(), 'PRODUCT_SUPPLIER_IDENTIFIER_CREATE', 'Create supplier product identifiers'),
    (gen_random_uuid(), 'PRODUCT_SUPPLIER_IDENTIFIER_UPDATE', 'Update supplier product identifiers'),
    (gen_random_uuid(), 'PRODUCT_SUPPLIER_IDENTIFIER_DELETE', 'Delete supplier product identifiers')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN (
    'PRODUCT_SUPPLIER_IDENTIFIER_VIEW',
    'PRODUCT_SUPPLIER_IDENTIFIER_CREATE',
    'PRODUCT_SUPPLIER_IDENTIFIER_UPDATE',
    'PRODUCT_SUPPLIER_IDENTIFIER_DELETE'
)
WHERE r.name IN ('SYSTEM_ADMIN', 'PROCUREMENT_MANAGER', 'PURCHASING_OFFICER')
  AND NOT EXISTS (
      SELECT 1
      FROM role_permissions rp
      WHERE rp.role_id = r.id
        AND rp.permission_id = p.id
  );
