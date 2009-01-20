/** UPGRADE TABLES AND COLUMNS DDL **/

alter table `pqfdata` 
	change `dataID` `id` bigint(20) unsigned   NOT NULL auto_increment first, 
	drop key `PRIMARY`, add PRIMARY KEY(`id`);

alter table `pqfdata` 
	add column `parentID` bigint(20) unsigned   NULL after `questionID`, 
	drop column `num`, 
	drop key `questionContractor`, add KEY `questionContractor`(`auditID`,`questionID`,`parentID`);

update pqfdata set parentID = null;


alter table `pqfquestions` 
	change `questionID` `id` smallint(6)   NOT NULL auto_increment first,
	drop key `PRIMARY`, add PRIMARY KEY(`id`);

alter table `pqfquestions` 
	add column `createdBy` int(11)   NULL after `question`, 
	add column `updatedBy` int(11)   NULL after `createdBy`, 
	change `dateCreated` `creationDate` datetime   NOT NULL after `updatedBy`, 
	change `lastModified` `updateDate` datetime   NOT NULL after `creationDate`, 
	add column `allowMultipleAnswers` tinyint(4)   NULL DEFAULT '0' after `isRequired`, 
	add column `parentID` smallint(5) unsigned   NULL after `isRedFlagQuestion`;

/**
 * Updated the questionType from Manual to Yes/No/NA and set showComment to true
 */

update pqfquestions set showComment = 1 
where questionType = 'Manual';

update pqfquestions set questionType = 'Yes/No/NA' 
where questionType = 'Manual';

/**
 * Adding radio Options (Green, Yellow, Red) to support integrity management2 audit
 * questions.
 */
insert into pqfoptions (questionID, optionName, visible, number)
values(?,?,?,?)
(2092, Green, Yes, 1)
(2092, Yellow, Yes, 5)
(2092, Red, Yes, 10)
(2093, Green, Yes, 1)
(2093, Yellow, Yes, 5)
(2093, Red, Yes, 10)
(2094, Green, Yes, 1)
(2094, Yellow, Yes, 5)
(2094, Red, Yes, 10)
(2095, Green, Yes, 1)
(2095, Yellow, Yes, 5)
(2095, Red, Yes, 10)
(2096, Green, Yes, 1)
(2096, Yellow, Yes, 5)
(2096, Red, Yes, 10)
(2097, Green, Yes, 1)
(2097, Yellow, Yes, 5)
(2097, Red, Yes, 10);


/**
 * remove the verification permissions for everyone who isn't pics
 */
delete from useraccess 
where userid in ( select u.id from users u where u.accountid != 1100 )
and accesstype = 'InsuranceVerification'

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
