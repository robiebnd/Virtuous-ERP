CREATE TABLE IF NOT EXISTS consolidation_journals (
 id UUID PRIMARY KEY,
 run_id UUID NOT NULL REFERENCES group_reporting_runs(id),
 journal_number VARCHAR(40) NOT NULL UNIQUE,
 journal_type VARCHAR(30) NOT NULL,
 description VARCHAR(255) NOT NULL,
 posting_date DATE NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'POSTED',
 total_debit NUMERIC(19,2) NOT NULL DEFAULT 0,
 total_credit NUMERIC(19,2) NOT NULL DEFAULT 0,
 posted_by VARCHAR(100) NOT NULL,
 posted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 reversal_of UUID REFERENCES consolidation_journals(id),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 version BIGINT NOT NULL DEFAULT 0
);
CREATE TABLE IF NOT EXISTS consolidation_journal_lines (
 id UUID PRIMARY KEY,
 journal_id UUID NOT NULL REFERENCES consolidation_journals(id),
 line_number INTEGER NOT NULL,
 account_code VARCHAR(30) NOT NULL,
 debit NUMERIC(19,2) NOT NULL DEFAULT 0,
 credit NUMERIC(19,2) NOT NULL DEFAULT 0,
 company_code VARCHAR(20),
 partner_company_code VARCHAR(20),
 description VARCHAR(255),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_consolidation_journal_line UNIQUE(journal_id,line_number)
);
CREATE TABLE IF NOT EXISTS consolidation_audit_events (
 id UUID PRIMARY KEY,
 group_id UUID NOT NULL REFERENCES consolidation_groups(id),
 run_id UUID REFERENCES group_reporting_runs(id),
 journal_id UUID REFERENCES consolidation_journals(id),
 event_type VARCHAR(40) NOT NULL,
 event_status VARCHAR(20) NOT NULL,
 event_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 actor VARCHAR(100) NOT NULL,
 reference_number VARCHAR(80),
 details TEXT,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 version BIGINT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_consolidation_audit_group_run ON consolidation_audit_events(group_id,run_id,event_time);
CREATE INDEX IF NOT EXISTS idx_consolidation_journal_run ON consolidation_journals(run_id,status);

CREATE TABLE IF NOT EXISTS copa_allocation_rules (
 id UUID PRIMARY KEY,
 company_code VARCHAR(20) NOT NULL,
 rule_code VARCHAR(50) NOT NULL,
 rule_name VARCHAR(150) NOT NULL,
 source_segment_id UUID NOT NULL REFERENCES profitability_segments(id),
 driver_type VARCHAR(30) NOT NULL,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_copa_allocation_rule UNIQUE(company_code,rule_code)
);
CREATE TABLE IF NOT EXISTS copa_allocation_targets (
 id UUID PRIMARY KEY,
 rule_id UUID NOT NULL REFERENCES copa_allocation_rules(id),
 target_segment_id UUID NOT NULL REFERENCES profitability_segments(id),
 allocation_percent NUMERIC(9,4) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT chk_copa_allocation_percent CHECK(allocation_percent > 0 AND allocation_percent <= 100)
);
CREATE TABLE IF NOT EXISTS copa_allocation_runs (
 id UUID PRIMARY KEY,
 rule_id UUID NOT NULL REFERENCES copa_allocation_rules(id),
 fiscal_year INTEGER NOT NULL,
 period_number INTEGER NOT NULL,
 allocated_amount NUMERIC(19,2) NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'POSTED',
 run_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 run_by VARCHAR(100) NOT NULL,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_copa_allocation_run UNIQUE(rule_id,fiscal_year,period_number)
);

CREATE TABLE IF NOT EXISTS tax_filing_records (
 id UUID PRIMARY KEY,
 company_code VARCHAR(20) NOT NULL,
 tax_type VARCHAR(40) NOT NULL,
 period_start DATE NOT NULL,
 period_end DATE NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
 taxable_base NUMERIC(19,2) NOT NULL DEFAULT 0,
 output_tax NUMERIC(19,2) NOT NULL DEFAULT 0,
 input_tax NUMERIC(19,2) NOT NULL DEFAULT 0,
 recoverable_input_tax NUMERIC(19,2) NOT NULL DEFAULT 0,
 net_tax NUMERIC(19,2) NOT NULL DEFAULT 0,
 filing_reference VARCHAR(100),
 filed_at TIMESTAMP,
 filed_by VARCHAR(100),
 notes TEXT,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_tax_filing_record UNIQUE(company_code,tax_type,period_start,period_end)
);
CREATE INDEX IF NOT EXISTS idx_tax_filing_company_period ON tax_filing_records(company_code,period_start,period_end,status);
