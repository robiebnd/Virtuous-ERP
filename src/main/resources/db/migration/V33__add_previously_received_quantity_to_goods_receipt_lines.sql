-- ==========================================================
-- V33 - GOODS RECEIPT PREVIOUSLY RECEIVED QUANTITY
-- Store the quantity that had already been received against
-- the Purchase Order Line before the current GRN was created.
--
-- This is historical data and must remain unchanged when the
-- GRN is approved. The cumulative quantity remains represented
-- by purchase_order_lines.received_quantity.
-- ==========================================================

ALTER TABLE goods_receipt_lines
    ADD COLUMN IF NOT EXISTS previously_received_quantity NUMERIC(18, 2) NOT NULL DEFAULT 0;

-- Backfill existing GRN lines using the approval sequence for
-- each Purchase Order Line. For each GRN:
--   previously received = cumulative accepted quantity before this GRN
--   current receipt     = this GRN's accepted quantity
--   cumulative received = sum through this GRN
--
-- Draft GRNs have no approved_at and therefore do not contribute
-- to the historical received quantity.
WITH receipt_sequence AS (
    SELECT
        grl.id,
        COALESCE(
            SUM(COALESCE(grl.accepted_quantity, 0)) OVER (
                PARTITION BY grl.purchase_order_line_id
                ORDER BY gr.approved_at NULLS LAST, gr.created_at, grl.id
                ROWS BETWEEN UNBOUNDED PRECEDING AND 1 PRECEDING
            ),
            0
        ) AS previous_received
    FROM goods_receipt_lines grl
    JOIN goods_receipts gr
        ON gr.id = grl.goods_receipt_id
)
UPDATE goods_receipt_lines grl
SET previously_received_quantity = rs.previous_received
FROM receipt_sequence rs
WHERE grl.id = rs.id;

COMMENT ON COLUMN goods_receipt_lines.previously_received_quantity IS
    'Quantity already received against the linked Purchase Order Line before this Goods Receipt.';
