-- A Stock Count line is loaded before the physical quantity is entered.
-- NULL therefore means "not yet counted"; zero remains a valid physical count.
ALTER TABLE stock_count_lines
    ALTER COLUMN counted_quantity DROP NOT NULL;
