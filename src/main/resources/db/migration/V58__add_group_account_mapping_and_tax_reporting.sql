CREATE TABLE IF NOT EXISTS group_account_mappings (
 id UUID PRIMARY KEY,
 group_id UUID NOT NULL REFERENCES consolidation_groups(id),
 company_code VARCHAR(20) NOT NULL REFERENCES company_codes(company_code),
 local_account_code VARCHAR(20) NOT NULL,
 group_account_code VARCHAR(20) NOT NULL,
 group_account_name VARCHAR(150) NOT NULL,
 group_account_type VARCHAR(30) NOT NULL,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_group_account_mapping UNIQUE(group_id,company_code,local_account_code)
);
CREATE INDEX IF NOT EXISTS idx_group_account_mapping_lookup ON group_account_mappings(group_id,company_code,local_account_code);

CREATE TABLE IF NOT EXISTS tax_reporting_periods (
 id UUID PRIMARY KEY,
 company_code VARCHAR(20) NOT NULL REFERENCES company_codes(company_code),
 period_start DATE NOT NULL,
 period_end DATE NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
 filed_at TIMESTAMP,
 filed_by VARCHAR(100),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT chk_tax_reporting_period_dates CHECK(period_end >= period_start),
 CONSTRAINT uq_tax_reporting_period UNIQUE(company_code,period_start,period_end)
);
CREATE INDEX IF NOT EXISTS idx_tax_reporting_period_company_status ON tax_reporting_periods(company_code,status);
