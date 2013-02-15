-- alter the tables
ALTER TABLE `invoice_commission` 
	MODIFY COLUMN `activationPoints` decimal(11,7) NOT NULL,
	MODIFY COLUMN `revenue` decimal(11,7) NOT NULL;
	
ALTER TABLE `payment_commission` 
	MODIFY COLUMN `activationPoints` decimal(11,7) NOT NULL;