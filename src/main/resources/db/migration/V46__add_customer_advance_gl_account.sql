INSERT INTO gl_accounts (id, account_code, account_name, account_type, control_account)
VALUES ('10000000-0000-0000-0000-000000000009','220000','Customer Advances / Unapplied Cash','LIABILITY',TRUE)
ON CONFLICT (account_code) DO NOTHING;
