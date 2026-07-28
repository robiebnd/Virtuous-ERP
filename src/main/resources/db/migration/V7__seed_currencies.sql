-- ==========================================================
-- V7__seed_currencies.sql
-- CURRENCY MASTER
-- ==========================================================

INSERT INTO currencies
(
    id,
    currency_code,
    currency_name,
    currency_symbol,
    decimal_places,
    is_base_currency,
    created_at,
    updated_at,
    active,
    version
)
VALUES

--------------------------------------------------------------
-- BASE CURRENCY
--------------------------------------------------------------

(
    gen_random_uuid(),
    'USD',
    'United States Dollar',
    '$',
    2,
    TRUE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- ZIMBABWE
--------------------------------------------------------------

(
    gen_random_uuid(),
    'ZWG',
    'Zimbabwe Gold',
    'ZiG',
    2,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- SOUTH AFRICA
--------------------------------------------------------------

(
    gen_random_uuid(),
    'ZAR',
    'South African Rand',
    'R',
    2,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- BOTSWANA
--------------------------------------------------------------

(
    gen_random_uuid(),
    'BWP',
    'Botswana Pula',
    'P',
    2,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- EUROPE
--------------------------------------------------------------

(
    gen_random_uuid(),
    'EUR',
    'Euro',
    '€',
    2,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- UNITED KINGDOM
--------------------------------------------------------------

(
    gen_random_uuid(),
    'GBP',
    'British Pound Sterling',
    '£',
    2,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- CHINA
--------------------------------------------------------------

(
    gen_random_uuid(),
    'CNY',
    'Chinese Yuan',
    '¥',
    2,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- JAPAN
--------------------------------------------------------------

(
    gen_random_uuid(),
    'JPY',
    'Japanese Yen',
    '¥',
    0,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- AUSTRALIA
--------------------------------------------------------------

(
    gen_random_uuid(),
    'AUD',
    'Australian Dollar',
    '$',
    2,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
),

--------------------------------------------------------------
-- CANADA
--------------------------------------------------------------

(
    gen_random_uuid(),
    'CAD',
    'Canadian Dollar',
    '$',
    2,
    FALSE,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    TRUE,
    0
)

ON CONFLICT (currency_code)
DO NOTHING;