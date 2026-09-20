CREATE TABLE IF NOT EXISTS copa_allocation_results (
 id UUID PRIMARY KEY,
 run_id UUID NOT NULL REFERENCES copa_allocation_runs(id),
 target_segment_id UUID NOT NULL REFERENCES profitability_segments(id),
 allocation_percent NUMERIC(9,4) NOT NULL,
 allocated_amount NUMERIC(19,2) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_copa_allocation_result UNIQUE(run_id,target_segment_id)
);
CREATE INDEX IF NOT EXISTS idx_copa_allocation_result_run ON copa_allocation_results(run_id);
