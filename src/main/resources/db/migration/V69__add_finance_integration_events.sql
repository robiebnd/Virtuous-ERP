INSERT INTO gl_accounts (id, account_code, account_name, account_type, control_account, active, created_at, updated_at, version)
VALUES (
    gen_random_uuid(),
    '530000',
    'Inventory Adjustment / Write-off',
    'EXPENSE',
    FALSE,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    0
)
ON CONFLICT (account_code) DO NOTHING;
