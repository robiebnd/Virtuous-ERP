CREATE TABLE IF NOT EXISTS consolidation_nci_results (
 id UUID PRIMARY KEY,
 run_id UUID NOT NULL REFERENCES group_reporting_runs(id),
 unit_id UUID NOT NULL REFERENCES consolidation_units(id),
 ownership_percent NUMERIC(7,4) NOT NULL,
 nci_percent NUMERIC(7,4) NOT NULL,
 net_assets NUMERIC(19,2) NOT NULL,
 nci_amount NUMERIC(19,2) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_consolidation_nci_run_unit UNIQUE(run_id,unit_id),
 CONSTRAINT chk_nci_ownership CHECK(ownership_percent>=0 AND ownership_percent<=100),
 CONSTRAINT chk_nci_percent CHECK(nci_percent>=0 AND nci_percent<=100)
);
ALTER TABLE group_reporting_runs ADD COLUMN IF NOT EXISTS nci_amount NUMERIC(19,2) NOT NULL DEFAULT 0;
CREATE INDEX IF NOT EXISTS idx_consolidation_nci_run ON consolidation_nci_results(run_id);
