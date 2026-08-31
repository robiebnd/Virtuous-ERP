-- Add cumulative receiving support for partial Goods Receipts.
-- previously_received_quantity represents the quantity received on earlier
-- approved GRNs for the same Purchase Order Line, before the current GRN.
ALTER TABLE goods_receipt_lines
    ADD COLUMN previously_received_quantity NUMERIC(19, 4) NOT NULL DEFAULT 0;

-- Backfill existing rows from approved historical Goods Receipts.
-- For each GRN line, previously_received_quantity is the sum of accepted
-- quantities from earlier approved GRNs for the same purchase order line.
UPDATE goods_receipt_lines current_line
SET previously_received_quantity = COALESCE((
    SELECT SUM(previous_line.accepted_quantity)
    FROM goods_receipt_lines previous_line
    JOIN goods_receipts previous_grn
      ON previous_grn.id = previous_line.goods_receipt_id
    WHERE previous_line.purchase_order_line_id = current_line.purchase_order_line_id
      AND previous_grn.status = 'APPROVED'
      AND previous_grn.received_date < (
          SELECT current_grn.received_date
          FROM goods_receipts current_grn
          WHERE current_grn.id = current_line.goods_receipt_id
      )
), 0);
