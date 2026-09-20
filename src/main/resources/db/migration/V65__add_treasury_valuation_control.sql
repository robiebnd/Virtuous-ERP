ALTER TABLE treasury_instruments
    ADD COLUMN IF NOT EXISTS last_valuation_date DATE;

CREATE INDEX IF NOT EXISTS idx_treasury_instruments_status_maturity
    ON treasury_instruments(status, maturity_date);

CREATE INDEX IF NOT EXISTS idx_treasury_instruments_counterparty_currency_type_status
    ON treasury_instruments(counterparty, currency, instrument_type, status);