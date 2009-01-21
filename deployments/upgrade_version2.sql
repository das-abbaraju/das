/** UPGRADE TABLES AND COLUMNS DDL **/

alter table `pqfdata` 
	change `dataID` `id` bigint(20) unsigned   NOT NULL auto_increment first, 
	drop key `PRIMARY`, add PRIMARY KEY(`id`);

alter table `pqfdata` 
	add column `parentID` bigint(20) unsigned   NULL after `questionID`, 
	drop column `num`, 
	drop key `questionContractor`, add KEY `questionContractor`(`auditID`,`questionID`,`parentID`);

alter table `pqfquestions` 
	change `questionID` `id` smallint(6)   NOT NULL auto_increment first,
	drop key `PRIMARY`, add PRIMARY KEY(`id`);

alter table `pqfquestions` 
	add column `createdBy` int(11)   NULL after `question`, 
	add column `updatedBy` int(11)   NULL after `createdBy`, 
	change `dateCreated` `creationDate` datetime   NOT NULL after `updatedBy`, 
	change `lastModified` `updateDate` datetime   NOT NULL after `creationDate`;

alter table `audit_type` 
	add column `mustVerify` tinyint(3)   NOT NULL DEFAULT '0' after `classType`, 
	change `hasRequirements` `hasRequirements` tinyint(3) unsigned   NOT NULL after `mustVerify`, 
	change `displayOrder` `displayOrder` tinyint(4)   NULL DEFAULT '100' after `hasRequirements`, 
	change `canContractorView` `canContractorView` tinyint(3) unsigned   NOT NULL after `hasAuditor`, COMMENT='';

alter table `contractor_info` 
	add column `viewedFacilities` datetime   NULL after `oqEmployees`, 
	add column `paymentMethod` varchar(20)  COLLATE latin1_swedish_ci NULL after `viewedFacilities`, 
	add column `paymentMethodStatus` varchar(20)  COLLATE latin1_swedish_ci NULL after `paymentMethod`;

rename table `certificates` to `certificates_old`;


/**
 * Changed the listType to Contractor if Certificate
 * 
 */
update email_template set listType = 'Contractor'
where templateID = 10;

/**
 * remove the verification permissions for everyone who isn't pics
 */
delete from useraccess 
where userid in ( select u.id from users u where u.accountid != 1100 )
and accesstype = 'InsuranceVerification'


/** Make sure these are set correctly **/

update pqfquestions set showComment = 1 
where questionType = 'Manual';

update pqfquestions set questionType = 'Yes/No/NA' 
where questionType = 'Manual';

-- Limits are all Flaggable
update pqfquestions
set isRedFlagQuestion = 'Yes'
where subCategoryID in (select subCatID from pqfsubcategories where subCategory = 'Policy Limits');

-- aiNames are tuples
update pqfquestions
set allowMultipleAnswers = 1, isRequired = 'No', isRedFlagQuestion = 'Yes', questionType = 'Additional Insured'
where uniqueCode = 'aiName';

update pqfquestions
set isRequired = 'Yes'
where uniqueCode = 'aiFile';

update pqfquestions
set isRequired = 'Yes', isRedFlagQuestion = 'Yes', questionType = 'Yes/No/NA'
where uniqueCode = 'aiWaiverSub';

update pqfquestions q1, pqfquestions q2
set q1.parentID = q2.id
where q1.uniqueCode like 'aiFile' and q2.uniqueCode like 'aiName'
and q1.subCategoryID = q2.subCategoryID;

update pqfquestions q1, pqfsubcategories s1, pqfsubcategories s2
set q1.subCategoryID = s2.subCatID
where q1.subCategoryID = s1.subCatID
and s1.categoryID = s2.categoryID and s2.number = 1
and uniqueCode = 'aiWaiverSub';

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
