-- ==========================================================
-- V16__align_putaway_tables_with_base_entity.sql
-- Align Put Away tables with BaseEntity optimistic locking.
-- Both PutAway and PutAwayLine extend BaseEntity and therefore
-- require the version column used by @Version.
-- ==========================================================

ALTER TABLE put_aways
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE put_away_lines
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

UPDATE put_aways
SET version = 0
WHERE version IS NULL;

UPDATE put_away_lines
SET version = 0
WHERE version IS NULL;
