CREATE TABLE IF NOT EXISTS fx_valuation_runs (
 id UUID PRIMARY KEY, reference_number VARCHAR(100) NOT NULL, valuation_date DATE NOT NULL, currency VARCHAR(3) NOT NULL,
 foreign_amount NUMERIC(19,6) NOT NULL, previous_rate NUMERIC(19,8) NOT NULL, closing_rate NUMERIC(19,8) NOT NULL,
 valuation_difference NUMERIC(19,2) NOT NULL, accounting_document_id UUID NOT NULL REFERENCES accounting_documents(id),
 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
 UNIQUE(reference_number,valuation_date)
);