DELETE FROM invoice_commission;

ALTER TABLE `invoice_commission` 
	ADD COLUMN `accountUserID` int(11) NOT NULL,
	MODIFY COLUMN `activationPoints` float(9,9) NOT NULL,
	MODIFY COLUMN `revenue` float(9,9) NOT NULL;

CREATE TABLE `commission_audit` (
	`invoiceID` int(11) NOT NULL,                                 
    `clientSiteID` int(11) NOT NULL,            
    `feeClass` varchar(50) CHARACTER SET latin1 NOT NULL,            
    PRIMARY KEY (`invoiceID`,`clientSiteID`,`feeClass`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;