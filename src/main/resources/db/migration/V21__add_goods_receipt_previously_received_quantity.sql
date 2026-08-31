ALTER TABLE goods_receipt_lines ADD COLUMN IF NOT EXISTS previously_received_quantity NUMERIC(18,2) NOT NULL DEFAULT 0;

WITH receipt_history AS (
    SELECT grl.id,
           COALESCE(SUM(grl.received_quantity) OVER (
               PARTITION BY grl.purchase_order_line_id
               ORDER BY gr.received_date, gr.created_at, grl.id
               ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING
           ), 0) AS previous_received
    FROM goods_receipt_lines grl
    JOIN goods_receipts gr ON gr.id = grl.goods_receipt_id
)
UPDATE goods_receipt_lines grl
SET previously_received_quantity = rh.previous_received
FROM receipt_history rh
WHERE grl.id = rh.id;
