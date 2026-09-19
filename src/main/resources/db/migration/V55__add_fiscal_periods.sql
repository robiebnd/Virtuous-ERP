CREATE TABLE IF NOT EXISTS fiscal_periods (
 id UUID PRIMARY KEY,
 company_code VARCHAR(20) NOT NULL,
 fiscal_year INTEGER NOT NULL,
 period_number INTEGER NOT NULL,
 period_name VARCHAR(40) NOT NULL,
 start_date DATE NOT NULL,
 end_date DATE NOT NULL,
 status VARCHAR(20) NOT NULL DEFAULT 'OPEN',
 closed_at TIMESTAMP,
 closed_by VARCHAR(120),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 active BOOLEAN NOT NULL DEFAULT TRUE,
 version BIGINT NOT NULL DEFAULT 0,
 CONSTRAINT uq_fiscal_period UNIQUE(company_code, fiscal_year, period_number),
 CONSTRAINT ck_fiscal_period_number CHECK (period_number BETWEEN 1 AND 12),
 CONSTRAINT ck_fiscal_period_status CHECK (status IN ('OPEN','CLOSED','PERMANENTLY_CLOSED'))
);
CREATE INDEX IF NOT EXISTS idx_fiscal_period_lookup ON fiscal_periods(company_code, fiscal_year, period_number, status);

INSERT INTO fiscal_periods (id, company_code, fiscal_year, period_number, period_name, start_date, end_date, status)
SELECT gen_random_uuid(), 'ZW01', EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER, m,
       to_char(make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER, m, 1), 'Mon YYYY'),
       make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER, m, 1),
       (make_date(EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER, m, 1) + INTERVAL '1 month - 1 day')::date,
       'OPEN'
FROM generate_series(1,12) AS m
ON CONFLICT (company_code, fiscal_year, period_number) DO NOTHING;