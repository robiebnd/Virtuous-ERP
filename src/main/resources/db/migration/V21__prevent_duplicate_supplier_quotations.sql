CREATE UNIQUE INDEX uq_supplier_quotations_pr_supplier_number
    ON supplier_quotations(purchase_requisition_id, supplier_id, quotation_number);
