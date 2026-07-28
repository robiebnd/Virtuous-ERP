INSERT INTO roles
(id,name,description)
VALUES
(gen_random_uuid(),'SYSTEM_ADMIN','System Administrator'),

(gen_random_uuid(),'WAREHOUSE_MANAGER','Warehouse Manager'),

(gen_random_uuid(),'WAREHOUSE_CLERK','Warehouse Clerk'),

(gen_random_uuid(),'PURCHASING_OFFICER','Purchasing Officer'),

(gen_random_uuid(),'PROCUREMENT_MANAGER','Procurement Manager'),

(gen_random_uuid(),'INVENTORY_CONTROLLER','Inventory Controller'),

(gen_random_uuid(),'SALES_MANAGER','Sales Manager'),

(gen_random_uuid(),'FINANCE_MANAGER','Finance Manager'),

(gen_random_uuid(),'AUDITOR','Read Only Auditor');