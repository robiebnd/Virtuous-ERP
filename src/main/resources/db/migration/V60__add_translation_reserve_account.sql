INSERT INTO gl_accounts (id,account_code,account_name,account_type,parent_account_id,posting_allowed,created_at,updated_at,active,version)
SELECT gen_random_uuid(),'3310','Foreign Currency Translation Reserve','EQUITY',NULL,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,TRUE,0
WHERE NOT EXISTS (SELECT 1 FROM gl_accounts WHERE account_code='3310');