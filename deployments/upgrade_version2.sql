/* Alter table in target */
ALTER TABLE `note` 
	CHANGE `summary` `summary` varchar(150)  COLLATE latin1_swedish_ci NOT NULL DEFAULT '' after `updatedBy`, 
	CHANGE `viewableBy` `viewableBy` int(10) unsigned   NOT NULL DEFAULT '1' after `priority`, 
	ADD COLUMN `username` varchar(200)  COLLATE latin1_swedish_ci NULL COMMENT 'temp column for conversion' after `attachment`, COMMENT='';

create table contractor_notes as 
select id, notes, adminnotes, 0 badNotes, 0 badAdminNotes from contractor_info;

update audit_type set classType = 'IM' where id = 17;

ALTER TABLE `contractor_info` 
	ADD COLUMN `ccOnFile` tinyint(3) unsigned   NOT NULL DEFAULT '0' after `paymentMethod`;

update contractor_info set paymentMethod = 'CreditCard' 
	where paymentMethod = 'Credit Card';

UPDATE contractor_info
set ccOnFile = 1
where paymentMethodStatus IN ('Approved','Valid')
and paymentMethod like 'Credit%Card';

/* Alter table in target */
ALTER TABLE `users` 
	CHANGE `phone` `phone` varchar(50)  COLLATE latin1_swedish_ci NULL after `resetHash`, 
	CHANGE `phoneIndex` `phoneIndex` varchar(10)  COLLATE latin1_swedish_ci NULL after `fax`, 
	CHANGE `passwordChanged` `passwordChanged` date   NULL after `phoneIndex`, 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `passwordChanged`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `createdBy`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `updatedBy`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `creationDate`, 
	ADD COLUMN `emailConfirmedDate` date   NULL after `updateDate`, 
	DROP COLUMN `faxIndex`, COMMENT='';

select * from accounts
join users using(username);

Delete from users
where accountID in (select id from accounts where type = 'Contractor');

insert into users 
      (username, password, isGroup, email, name, isActive, lastLogin, accountID, phone, fax, passwordChanged, emailConfirmedDate, createdBy, updatedBy, creationDate, updateDate)
select username, password, 'No', email, contact, 'Yes', lastLogin, id, phone, fax, passwordChange, emailConfirmedDate, createdBy, updatedBy, creationDate, updateDate
from accounts where type = 'Contractor';

/* Alter table in target */
ALTER TABLE `accounts` 
	CHANGE `lastLogin` `lastLogin` datetime   NULL after `name`, 
	CHANGE `contact` `contact` varchar(50)  COLLATE latin1_swedish_ci NULL after `lastLogin`, 
	CHANGE `address` `address` varchar(50)  COLLATE latin1_swedish_ci NULL after `contact`, 
	CHANGE `city` `city` varchar(50)  COLLATE latin1_swedish_ci NULL after `address`, 
	CHANGE `state` `state` char(2)  COLLATE latin1_swedish_ci NULL after `city`, 
	CHANGE `zip` `zip` varchar(50)  COLLATE latin1_swedish_ci NULL after `state`, 
	CHANGE `phone` `phone` varchar(50)  COLLATE latin1_swedish_ci NULL after `zip`, 
	CHANGE `phone2` `phone2` varchar(50)  COLLATE latin1_swedish_ci NULL after `phone`, 
	CHANGE `fax` `fax` varchar(20)  COLLATE latin1_swedish_ci NULL after `phone2`, 
	CHANGE `email` `email` varchar(50)  COLLATE latin1_swedish_ci NULL after `fax`, 
	CHANGE `web_URL` `web_URL` varchar(50)  COLLATE latin1_swedish_ci NULL after `email`, 
	CHANGE `industry` `industry` varchar(50)  COLLATE latin1_swedish_ci NULL after `web_URL`, 
	CHANGE `active` `active` char(1)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'N' after `industry`, 
	CHANGE `seesAll_B` `seesAll_B` char(1)  COLLATE latin1_swedish_ci NULL DEFAULT 'N' after `active`, 
	CHANGE `sendActivationEmail_B` `sendActivationEmail_B` char(1)  COLLATE latin1_swedish_ci NULL DEFAULT 'N' after `seesAll_B`, 
	CHANGE `activationEmails_B` `activationEmails_B` varchar(155)  COLLATE latin1_swedish_ci NULL after `sendActivationEmail_B`, 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `activationEmails_B`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `createdBy`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `updatedBy`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `creationDate`, 
	DROP COLUMN `username`, 
	DROP COLUMN `password`, 
	DROP COLUMN `passwordChange`, 
	DROP COLUMN `emailConfirmedDate`, 
	DROP KEY `username`, COMMENT='';

update contractor_info set membershipLevelID = 3 where billingAmount = 0 OR billingAmount IS NULL;
update contractor_info set membershipLevelID = 4 where billingAmount = 99;
update contractor_info set membershipLevelID = 5 where billingAmount = 399;
update contractor_info set membershipLevelID = 6 where billingAmount = 699;
update contractor_info set membershipLevelID = 7 where billingAmount = 999;
update contractor_info set membershipLevelID = 8 where billingAmount = 1299;
update contractor_info set membershipLevelID = 9 where billingAmount = 1699;
update contractor_info set membershipLevelID = 10 where billingAmount = 1999;

update contractor_info set newMembershipLevelID = 3 where newBillingAmount = 0 OR newBillingAmount IS NULL;
update contractor_info set newMembershipLevelID = 4 where newBillingAmount = 99;
update contractor_info set newMembershipLevelID = 5 where newBillingAmount = 399;
update contractor_info set newMembershipLevelID = 6 where newBillingAmount = 699;
update contractor_info set newMembershipLevelID = 7 where newBillingAmount = 999;
update contractor_info set newMembershipLevelID = 8 where newBillingAmount = 1299;
update contractor_info set newMembershipLevelID = 9 where newBillingAmount = 1699;
update contractor_info set newMembershipLevelID = 10 where newBillingAmount = 1999;

update contractor_info set renew = 0 
	where id in (select id from accounts where active = 'N');
update contractor_info set membershipLevelID = newMembershipLevelID 
	where membershipLevelID is null and mustPay = 'Yes' and billingAmount > 0;
update contractor_info set renew = 1
	where accountDate is null OR accountDate = '0000-00-00';


/******* INVOICES *************/
update contractor_info set lastPayment = '0000-00-00' where lastPayment is null;
update contractor_info set lastInvoiceDate = '0000-00-00' where lastInvoiceDate is null;

-- Add all paid invoices
insert into invoice (accountID, creationDate, createdBy, totalAmount, paid, paidDate)
select c.id, lastPayment, 1, lastPaymentAmount, 1, lastPayment
from contractor_info c
where lastPaymentAmount > 0 and (lastPayment > '0000-00-00');

-- Add all unpaid invoices
insert into invoice (accountID, creationDate, createdBy, totalAmount, paid)
select c.id, lastInvoiceDate, 1, billingAmount, 0
from contractor_info c
where billingAmount > 0 and lastInvoiceDate > '0000-00-00'
and lastInvoiceDate > lastPayment;

update invoice set dueDate = adddate(creationDate, INTERVAL 1 MONTH);


insert into invoice_item (invoiceID, creationDate, createdBy, amount)
select id, creationDate, createdBy, totalAmount from invoice;

update invoice_item set feeID = 4 where amount = 99;
update invoice_item set feeID = 5 where amount = 399;
update invoice_item set feeID = 6 where amount = 699;
update invoice_item set feeID = 7 where amount = 999;
update invoice_item set feeID = 8 where amount = 1299;
update invoice_item set feeID = 9 where amount = 1699;
update invoice_item set feeID = 10 where amount = 1999;

update contractor_info
set paymentExpires = adddate(lastPayment, interval 1 year)
where (paymentExpires = '0000-00-00' or paymentExpires is null) and lastPayment > '0000-00-00';

update contractor_info, accounts
set paymentExpires = creationDate
where (paymentExpires = '0000-00-00' or paymentExpires is null)
and contractor_info.id = accounts.id;

/**** Notes Conversion *****/

update notes n, users u
set n.userID = u.id
where n.opID = u.accountID and n.whoIs = u.name and (userID is null or userID = 0);

update notes n, users u
set n.userID = u.id
where 1100 = u.accountID and n.whoIs = u.name and (userID is null or userID = 0);

update notes set userID = 38 where opID = 950 AND whoIs = 'Carla Mancera';
update notes set userID = 1 where opID = 950 AND whoIs = 'Diana Williams';
update notes set userID = 73 where opID = 969 AND whoIs = 'jared';
update notes set userID = 1588 where opID = 969 AND whoIs = 'PICS Tester';
update notes set userID = 611 where opID = 1195 AND whoIs = 'John Buchanan';
update notes set userID = 1407 where opID = 1204 AND whoIs = 'Donald Slack';
update notes set userID = 974 where opID = 1436 AND whoIs = 'Mike MCardle';
update notes set userID = 1 where opID = 2073 AND whoIs = 'Bonnie Fischerkeller';
update notes set userID = 1 where opID = 2073 AND whoIs = 'Chevron Hawaii Test';
update notes set userID = 714 where opID = 2175 AND whoIs = 'Mike Heckel';
update notes set userID = 584 where opID = 2175 AND whoIs = 'raymond.dixon@shell.com';
update notes set userID = 1 where opID = 2921 AND whoIs = 'Ellen Gallo';
update notes set userID = 1 where opID = 2921 AND whoIs = 'PICS PICS';
update notes set userID = 1 where opID = 2921 AND whoIs = 'Todd Johnson';


/*
insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select 
	id as accountID, 
	accountDate as creationDate,
	959 as createdBy,
	'Contractor Notes Pre-2009' as summary,
	'General' as noteCategory,
	2 as priorityMed,
	1 as viewableByEveryone,
	notes
from contractor_info
where notes > '';

insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select 
	id, 
	accountDate, 
	959, 
	'PICS-only Notes Pre-2009',
	'Billing', 
	2 as priorityMed,
	1 as viewableByPics,
	adminNotes
from contractor_info
where adminNotes > '';
*/

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy)
select 
	conID as accountID,
	timeStamp as creationDate, 
	case ISNULL(userID) when 1 then 959 else userID end,
	deletedDate,
	deletedUserID,
	note as summary,
	'General' as noteCategory,
	case isDeleted when 1 then 0 else 2 end as status,
	2 as priorityMed,
	opID as viewableBy
from notes
where length(note) <= 100;

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy, body)
select conID, timeStamp, case ISNULL(userID) when 1 then 959 else userID end, deletedDate, deletedUserID, substring(note, 1, 100), 'General', case isDeleted when 1 then 0 else 2 end, 2, opID, substring(note, 100)
from notes
where length(note) > 100;

/* Alter table in target */
ALTER TABLE `contractor_info` 
	CHANGE `logo_file` `logo_file` varchar(50)  COLLATE latin1_swedish_ci NULL after `main_trade`, 
	CHANGE `brochure_file` `brochure_file` varchar(10)  COLLATE latin1_swedish_ci NULL after `logo_file`, 
	CHANGE `description` `description` text  COLLATE latin1_swedish_ci NULL after `brochure_file`, 
	CHANGE `accountDate` `accountDate` date   NULL after `description`, 
	CHANGE `mustPay` `mustPay` enum('Yes','No')  COLLATE latin1_swedish_ci NULL DEFAULT 'No' after `accountDate`, 
	CHANGE `paymentExpires` `paymentExpires` date   NULL after `mustPay`, 
	CHANGE `requestedByID` `requestedByID` mediumint(9)   NULL DEFAULT '0' after `paymentExpires`, 
	CHANGE `secondContact` `secondContact` varchar(50)  COLLATE latin1_swedish_ci NULL after `requestedByID`, 
	CHANGE `secondPhone` `secondPhone` varchar(50)  COLLATE latin1_swedish_ci NULL after `secondContact`, 
	CHANGE `secondEmail` `secondEmail` varchar(50)  COLLATE latin1_swedish_ci NULL after `secondPhone`, 
	CHANGE `billingContact` `billingContact` varchar(50)  COLLATE latin1_swedish_ci NULL after `secondEmail`, 
	CHANGE `billingPhone` `billingPhone` varchar(50)  COLLATE latin1_swedish_ci NULL after `billingContact`, 
	CHANGE `billingEmail` `billingEmail` varchar(50)  COLLATE latin1_swedish_ci NULL after `billingPhone`, 
	CHANGE `membershipDate` `membershipDate` date   NULL after `billingEmail`, 
	CHANGE `payingFacilities` `payingFacilities` smallint(5) unsigned   NOT NULL DEFAULT '0' after `membershipDate`, 
	CHANGE `welcomeAuditor_id` `welcomeAuditor_id` mediumint(8) unsigned   NULL after `payingFacilities`, 
	CHANGE `riskLevel` `riskLevel` tinyint(3) unsigned   NOT NULL DEFAULT '2' after `welcomeAuditor_id`, 
	CHANGE `oqEmployees` `oqEmployees` enum('No','Yes')  COLLATE latin1_swedish_ci NULL after `riskLevel`, 
	CHANGE `viewedFacilities` `viewedFacilities` datetime   NULL after `oqEmployees`, 
	CHANGE `paymentMethod` `paymentMethod` varchar(20)  COLLATE latin1_swedish_ci NULL DEFAULT 'CreditCard' after `viewedFacilities`, 
	CHANGE `paymentMethodStatus` `paymentMethodStatus` varchar(20)  COLLATE latin1_swedish_ci NULL after `paymentMethod`, 
	CHANGE `membershipLevelID` `membershipLevelID` smallint(6)   NULL after `paymentMethodStatus`, 
	CHANGE `newMembershipLevelID` `newMembershipLevelID` smallint(6)   NULL after `membershipLevelID`, 
	CHANGE `renew` `renew` tinyint(4)   NULL DEFAULT '1' after `newMembershipLevelID`, 
	CHANGE `lastUpgradeDate` `lastUpgradeDate` date   NULL after `renew`, 
	CHANGE `balance` `balance` mediumint(9)   NULL DEFAULT '0' after `lastUpgradeDate`, 
	ADD COLUMN `needsRecalculation` tinyint(4)   NOT NULL DEFAULT '1' after `balance`, 
	ADD COLUMN `lastRecalculation` datetime   NULL after `needsRecalculation`, 
	DROP COLUMN `trades`, 
	DROP COLUMN `subTrades`, 
	DROP COLUMN `paid`, 
	DROP COLUMN `lastPayment`, 
	DROP COLUMN `lastPaymentAmount`, 
	DROP COLUMN `lastInvoiceDate`, 
	DROP COLUMN `accountNewComplete`, 
	DROP COLUMN `notes`, 
	DROP COLUMN `adminNotes`, 
	DROP COLUMN `lastAnnualUpdateEmailDate`, 
	DROP COLUMN `billingCycle`, 
	DROP COLUMN `billingAmount`, 
	DROP COLUMN `isExempt`, 
	DROP COLUMN `isOnlyCerts`, 
	DROP COLUMN `newBillingAmount`, 
	DROP COLUMN `annualUpdateEmails`, 
	DROP KEY `isOnlyCerts`, COMMENT='';

update contractor_info set needsRecalculation = 0;

insert into useraccess set 
	userID = 958, accessType = 'Billing', 
	viewFlag = 1, editFlag = 1, deleteFlag = 1, grantFlag = 0, 
	lastUpdate = NOW(), grantedByID = 941;

/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;

*/
