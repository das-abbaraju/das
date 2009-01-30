alter table users
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

update users set creationDate = dateCreated;

alter table users
	drop column `dateCreated`;

alter table `usergroup` 
	change `userGroupID` `id` bigint(20) unsigned   NOT NULL auto_increment first;

alter table usergroup
	add column `updatedBy` int(11)   NULL, 
	add column `updateDate` datetime   NULL;

alter table `pqfsubcategories` 
	change `subCatID` `id` smallint(6) unsigned   NOT NULL auto_increment first;

alter table pqfsubcategories
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table pqfquestion_operator
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `pqfoptions` 
	change `optionID` `id` smallint(6) unsigned   NOT NULL auto_increment first;

alter table pqfoptions
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table pqfopmatrix
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `pqfcategories` 
	change `catID` `id` smallint(6) unsigned   NOT NULL auto_increment first;

alter table pqfcategories
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `pqfcatdata` 
	change `catDataID` `id` int(6) unsigned   NOT NULL auto_increment first;

alter table pqfcatdata
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `operatorforms` 
	change `formID` `id` smallint unsigned   NOT NULL auto_increment first;

alter table operatorforms
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `note` 
	change `noteID` `id` int unsigned   NOT NULL auto_increment first;

alter table generalcontractors
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

update generalcontractors set creationDate = dateAdded;

alter table generalcontractors
	drop column `dateAdded`;

alter table `flagcriteria` 
	change `criteriaID` `id` mediumint unsigned   NOT NULL auto_increment first;

alter table flagcriteria
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `flagoshacriteria` 
	change `criteriaID` `id` mediumint unsigned   NOT NULL auto_increment first;

alter table flagoshacriteria
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `facilities` 
	change `facilityID` `id` mediumint unsigned   NOT NULL auto_increment first;

alter table facilities
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `email_template` 
	change `templateID` `id` mediumint unsigned   NOT NULL auto_increment first;

alter table desktopmatrix
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `contractor_audit` 
	change `auditID` `id` int unsigned   NOT NULL auto_increment first;

alter table `contractor_audit` 
	change `createdDate` `creationDate` datetime   NULL;

alter table contractor_audit
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `updateDate` datetime   NULL;

alter table `audit_operator` 
	change `auditOperatorID` `id` int unsigned   NOT NULL auto_increment first;

alter table `audit_operator` 
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

delete from app_properties
where property like 'email%' or property = 'DEFAULT_SIGNATURE';

update accounts set creationDate = dateCreated;

alter table accounts
	drop column `createdBy`,
	drop column `dateCreated`;


alter table `accounts` 
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;

alter table `audit_type` 
	change `auditTypeID` `id` int unsigned   NOT NULL auto_increment first;

alter table `audit_type` 
	add column `createdBy` int(11)   NULL, 
	add column `updatedBy` int(11)   NULL, 
	add column `creationDate` datetime   NULL, 
	add column `updateDate` datetime   NULL;


/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;


update notes set userID from whois and opID
update notes set deletedUserID from whoDeleted and opID


insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select id, accountDate, 959, 'Contractor Notes Pre-Oct08', 'General', 3, 1, notes
from contractor_info
where notes > '';

insert into note (accountID, creationDate, createdBy, summary, noteCategory, priority, viewableBy, body)
select id, accountDate, 959, 'PICS-only Notes Pre-Oct08', 'General', 3, 1100, adminNotes
from contractor_info
where adminNotes > '';

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy, body)
select conID, timeStamp, case ISNULL(userID) when 1 then 959 else userID end, deletedDate, deletedUserID, note, 'General', case isDeleted when 1 then 0 else 2 end, 3, opID, null
from notes
where length(note) <= 250;

insert into note (accountID, creationDate, createdBy, updatedBy, updateDate, summary, noteCategory, status, priority, viewableBy, body)
select conID, timeStamp, case ISNULL(userID) when 1 then 959 else userID end, deletedDate, deletedUserID, substring(note, 1, 255), 'General', case isDeleted when 1 then 0 else 2 end, 3, opID, substring(note, 255)
from notes
where length(note) > 250;
*/
