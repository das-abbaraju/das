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

ALTER TABLE `payment_commission` 
	MODIFY COLUMN `activationPoints` decimal(9,7) NOT NULL;

-- Seed the SalesCommission permissions for specific users
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (46726, 'SalesCommission', 1, 1, 1, 1, NOW(), 53137);
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (941, 'SalesCommission', 1, 1, 1, 1, NOW(), 53137);
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (1650, 'SalesCommission', 1, 1, 1, 1, NOW(), 53137);
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (35364, 'SalesCommission', 1, 1, 1, 1, NOW(), 53137);
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (926, 'SalesCommission', 1, 1, 1, 1, NOW(), 53137);
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (928, 'SalesCommission', 1, 1, 1, 1, NOW(), 53137);
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (81385, 'SalesCommission', 1, 1, 1, 1, NOW(), 53137);
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (53137, 'SalesCommission', 1, 1, 1, 1, NOW(), 53137);
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (77314, 'SalesCommission', 1, 1, 1, 1, NOW(), 53137);
INSERT INTO useraccess (userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID) VALUES (940, 'SalesCommission', 1, 0, 0, 0, NOW(), 53137);