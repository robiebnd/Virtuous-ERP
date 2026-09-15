-- Replace predictable legacy product SKUs with internally assigned-looking identifiers.
-- Existing UUIDs remain unchanged; only the business SKU changes.
-- The mappings are deliberately explicit so existing procurement/inventory records
-- continue to reference the same Product rows.

UPDATE products
SET sku = 'SKU-482731'
WHERE sku = 'PRD004'
  AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SKU-482731');

UPDATE products
SET sku = 'SKU-913624'
WHERE sku = 'PRD005'
  AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SKU-913624');

UPDATE products
SET sku = 'SKU-205817'
WHERE sku = 'PRD006'
  AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SKU-205817');

UPDATE products
SET sku = 'SKU-731946'
WHERE sku = 'PRD007'
  AND NOT EXISTS (SELECT 1 FROM products WHERE sku = 'SKU-731946');
