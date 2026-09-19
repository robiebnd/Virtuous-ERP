INSERT INTO company_codes (id,company_code,company_name,country_code,functional_currency,reporting_currency)
SELECT gen_random_uuid(),u.company_code,COALESCE(MAX(u.unit_name),'Imported Entity'),'ZWE','USD','USD'
FROM consolidation_units u
WHERE NOT EXISTS (SELECT 1 FROM company_codes c WHERE c.company_code=u.company_code)
GROUP BY u.company_code;

ALTER TABLE consolidation_units ADD CONSTRAINT fk_consolidation_unit_company FOREIGN KEY (company_code) REFERENCES company_codes(company_code);
ALTER TABLE accounting_lines ADD CONSTRAINT fk_accounting_line_partner_company FOREIGN KEY (partner_company_code) REFERENCES company_codes(company_code);
ALTER TABLE accounting_lines ADD CONSTRAINT fk_accounting_line_profitability_segment FOREIGN KEY (profitability_segment_id) REFERENCES profitability_segments(id);
ALTER TABLE intercompany_transactions ADD CONSTRAINT fk_ic_source_company FOREIGN KEY (source_company_code) REFERENCES company_codes(company_code);
ALTER TABLE intercompany_transactions ADD CONSTRAINT fk_ic_target_company FOREIGN KEY (target_company_code) REFERENCES company_codes(company_code);
ALTER TABLE tax_postings ADD CONSTRAINT fk_tax_posting_company FOREIGN KEY (company_code) REFERENCES company_codes(company_code);