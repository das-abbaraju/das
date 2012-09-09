-- PICS-3666 
-- Adding support for an API key to be used with the REST API to link the request to a (special) user account.

ALTER TABLE `users` 
	add column `api` tinyint(4) NOT NULL DEFAULT '0',                                                                
    add column `apiKey` varchar(36) DEFAULT NULL,
    add key `apiKey` (`apiKey`);    

insert into `users` (`username`, `password`, `isGroup`, `name`, `isActive`, `accountID`, `createdBy`, `updatedBy`, `creationDate`, `updateDate`,`timezone`, `forcePasswordReset`, `needsIndexing`, `locale`) values('GROUP1100PICS API Approver','14f9f54924f300e942565e0b3e3722f5e1efb1f0','Yes','PICS API Approver','Yes','1100','63932','63932',now(),now(),'US/Central','0','1','en_US');
