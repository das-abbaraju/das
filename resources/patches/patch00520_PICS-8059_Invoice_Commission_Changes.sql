ALTER TABLE `invoice_commission` 
	ADD COLUMN `accountUserID` int(11) NOT NULL,
	MODIFY COLUMN `activationPoints` decimal(9,7) NOT NULL,
	MODIFY COLUMN `revenue` decimal(9,7) NOT NULL,
	MODIFY COLUMN `userID` int(11) DEFAULT NULL;

CREATE TABLE `commission_audit` (
	`invoiceID` int(11) NOT NULL,                                 
    `clientSiteID` int(11) NOT NULL,            
    `feeClass` varchar(50) CHARACTER SET latin1 NOT NULL,            
    PRIMARY KEY (`invoiceID`,`clientSiteID`,`feeClass`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;