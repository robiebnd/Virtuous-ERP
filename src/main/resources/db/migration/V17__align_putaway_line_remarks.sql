-- ==========================================================
-- V17__align_putaway_line_remarks.sql
-- Align PutAwayLine with the current JPA entity.
-- PutAwayLine declares a remarks field mapped to the remarks
-- column, but the existing table was created without it.
-- ==========================================================

ALTER TABLE put_away_lines
    ADD COLUMN IF NOT EXISTS remarks VARCHAR(500);
