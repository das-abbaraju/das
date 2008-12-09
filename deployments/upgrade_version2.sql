/** UPGRADE TABLES AND COLUMNS DDL **/

alter table `audit_operator` 
	add column `requiredAuditStatus` varchar(15)
		COLLATE latin1_swedish_ci NULL DEFAULT 'Active' after `requiredForFlag`;

alter table `contractor_audit` 
	add column `auditFor` varchar(50)  COLLATE latin1_swedish_ci NULL after `manuallyAdded`, 
	drop key `auditTypeID_conID_createdDate`, 
	add KEY `auditTypeID_conID_createdDate`(`conID`,`auditTypeID`);

alter table `flagcriteria` 
	add column `multiYearScope` enum('LastYearOnly','ThreeYearAverage','AllThreeYears')  
		COLLATE latin1_swedish_ci NULL after `validationRequired`;

create table `osha_audit`(
	`id` smallint(6) unsigned NOT NULL  auto_increment  , 
	`auditID` mediumint(8) unsigned NOT NULL   , 
	`SHAType` enum('OSHA','MSHA') COLLATE latin1_swedish_ci NOT NULL  DEFAULT 'OSHA'  , 
	`location` varchar(100) COLLATE latin1_swedish_ci NULL   , 
	`description` varchar(250) COLLATE latin1_swedish_ci NULL   , 
	`auditorID` smallint(6) NULL  DEFAULT '0'  , 
	`verifiedDate` date NULL   , 
	`applicable` tinyint(4) NULL  DEFAULT '1'  , 
	`fileUploaded` tinyint(4) NULL  DEFAULT '1'  , 
	`manHours` int(10) unsigned NULL  DEFAULT '0'  , 
	`fatalities` tinyint(3) unsigned NULL  DEFAULT '0'  , 
	`lostWorkCases` smallint(5) unsigned NULL  DEFAULT '0'  , 
	`lostWorkDays` smallint(5) unsigned NULL  DEFAULT '0'  , 
	`injuryIllnessCases` smallint(5) unsigned NULL  DEFAULT '0'  , 
	`restrictedWorkCases` smallint(5) unsigned NULL  DEFAULT '0'  , 
	`recordableTotal` smallint(5) unsigned NULL  DEFAULT '0'  , 
	`comment` varchar(250) COLLATE latin1_swedish_ci NULL   , 
	`creationDate` datetime NULL   , 
	`updateDate` datetime NULL   , 
	PRIMARY KEY (`id`) , 
	KEY `auditID`(`auditID`,`SHAType`,`location`) 
)Engine=MyISAM DEFAULT CHARSET='latin1';


alter table `pqfdata` 
	change `auditorID` `auditorID` smallint(5) unsigned   NULL after `dateVerified`, 
	change `wasChanged` `wasChanged` enum('No','Yes')  
		COLLATE latin1_swedish_ci NULL DEFAULT 'No' after `auditorID`, 
	add column `createdBy` int(11)   NULL after `wasChanged`, 
	add column `creationDate` datetime   NULL after `createdBy`, 
	add column `updatedBy` int(11)   NULL after `creationDate`, 
	add column `updateDate` datetime   NULL after `updatedBy`;



/** Add the Osha/EMR audit to all operators based on their PQF preferences **/

delete from audit_operator where auditTypeID = 11;

insert into audit_operator (auditTypeID, opID, minRiskLevel, requiredForFlag, requiredAuditStatus)
select 11, ao.opID, min(riskLevel), requiredForFlag, requiredAuditStatus
from audit_operator ao
join pqfopmatrix pm using (opID)
where ao.auditTypeID = 1 and pm.catID in (10,29)
group by ao.opID;

delete from contractor_audit
where auditTypeID = 11;

/** Create Osha/EMR audits for all contractors for years 2001-2007 **/

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus, auditorID, completedDate, closedDate, manuallyAdded)
select '2007-12-31', '2007', 11, conID, auditStatus, auditorID, completedDate, closedDate, 0
from contractor_audit ca
where auditTypeID = 1;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus, auditorID, completedDate, closedDate, manuallyAdded)
select '2006-12-31', '2006', 11, conID, auditStatus, auditorID, completedDate, closedDate, 0
from contractor_audit ca
where auditTypeID = 1;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus, auditorID, completedDate, closedDate, manuallyAdded)
select '2005-12-31', '2005', 11, conID, auditStatus, auditorID, completedDate, closedDate, 0
from contractor_audit ca
where auditTypeID = 1;



insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus)
select distinct '2004-12-31', '2004', 11, conID, 'Expired'
from osha where manHours4 > 0;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus)
select distinct '2003-12-31', '2003', 11, conID, 'Expired'
from osha where manHours5 > 0;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus)
select distinct '2002-12-31', '2002', 11, conID, 'Expired'
from osha where manHours6 > 0;

insert into contractor_audit (createdDate, auditFor, auditTypeID, conID, auditStatus)
select distinct '2001-12-31', '2001', 11, conID, 'Expired'
from osha where manHours7 > 0;


update contractor_audit
set expiresDate = ADDDATE(ADDDATE(createdDate, INTERVAL 1 DAY), INTERVAL 38 MONTH)
where auditTypeID = 11;


insert into osha_audit (auditID, SHAType, location, description, auditorID, 
verifiedDate, applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal, comment)
select ca.auditID, SHAType, location, description, osha.auditorID, 
verifiedDate1, case NA1 when 'Yes' then 0 else 1 end, case file1YearAgo when 'Yes' then 1 else 0 end, manHours1, fatalities1, lostWorkCases1, 
lostWorkDays1, injuryIllnessCases1, restrictedWorkCases1, recordableTotal1, comment1
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2007' and manHours1 > 0;

insert into osha_audit (auditID, SHAType, location, description, auditorID, 
verifiedDate, applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal, comment)
select ca.auditID, SHAType, location, description, osha.auditorID, 
verifiedDate2, case NA2 when 'Yes' then 0 else 1 end, case file2YearAgo when 'Yes' then 1 else 0 end, manHours2, fatalities2, lostWorkCases2, 
lostWorkDays2, injuryIllnessCases2, restrictedWorkCases2, recordableTotal2, comment2
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2006' and manHours2 > 0;

insert into osha_audit (auditID, SHAType, location, description, auditorID, 
verifiedDate, applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal, comment)
select ca.auditID, SHAType, location, description, osha.auditorID, 
verifiedDate3, case NA3 when 'Yes' then 0 else 1 end, case file3YearAgo when 'Yes' then 1 else 0 end, manHours3, fatalities3, lostWorkCases3, 
lostWorkDays3, injuryIllnessCases3, restrictedWorkCases3, recordableTotal3, comment3
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2005' and manHours3 > 0;


insert into osha_audit (auditID, SHAType, location, description,
applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal)
select ca.auditID, SHAType, location, description, 
1, case file4YearAgo when 'Yes' then 1 else 0 end, manHours4, fatalities4, lostWorkCases4,
lostWorkDays4, injuryIllnessCases4, restrictedWorkCases4, recordableTotal4
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2004' and manHours4 > 0;


insert into osha_audit (auditID, SHAType, location, description,
applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal)
select ca.auditID, SHAType, location, description, 
1, case file5YearAgo when 'Yes' then 1 else 0 end, manHours5, fatalities5, lostWorkCases5,
lostWorkDays5, injuryIllnessCases5, restrictedWorkCases5, recordableTotal5
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2003' and manHours5 > 0;


insert into osha_audit (auditID, SHAType, location, description,
applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal)
select ca.auditID, SHAType, location, description, 
1, case file6YearAgo when 'Yes' then 1 else 0 end, manHours6, fatalities6, lostWorkCases6,
lostWorkDays6, injuryIllnessCases6, restrictedWorkCases6, recordableTotal6
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2002' and manHours6 > 0;


insert into osha_audit (auditID, SHAType, location, description,
applicable, fileUploaded, manHours, fatalities, lostWorkCases, 
lostWorkDays, injuryIllnessCases, restrictedWorkCases, recordableTotal)
select ca.auditID, SHAType, location, description, 
1, case file7YearAgo when 'Yes' then 1 else 0 end, manHours7, fatalities7, lostWorkCases7,
lostWorkDays7, injuryIllnessCases7, restrictedWorkCases7, recordableTotal7
from osha
join contractor_audit ca on ca.conID = osha.conID and auditTypeID = 11
where auditFor = '2001' and manHours7 > 0;


update osha_audit 
set auditorID = null
where auditorID = 0;



delete from pqfdata where auditID in (select auditID from contractor_audit where auditTypeID = 11);

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2034, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2007' and d.questionID = 1617;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2034, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2006' and d.questionID = 1519;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2034, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2005' and d.questionID = 889;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2034, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2004' and d.questionID = 126;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2034, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2003' and d.questionID = 127;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2037, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2007' and d.questionID = 1618;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2037, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2006' and d.questionID = 1522;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2037, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2005' and d.questionID = 872;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2033, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2007' and d.questionID = 891;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2033, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2006' and d.questionID = 891;

insert into pqfdata (auditID, questionID, answer, comment, dateVerified, verifiedAnswer, auditorID, isCorrect)
select emr.auditID, 2033, d.answer, d.comment, d.dateVerified, d.verifiedAnswer, d.auditorID, d.isCorrect
from pqfdata d
join contractor_audit pqf on pqf.auditID = d.auditID
join contractor_audit emr on pqf.conID = emr.conID and emr.auditTypeID = 11
where emr.auditFor = '2005' and d.questionID = 891;

delete from pqfdata where questionID in (127,126,889,1519,872,1522,1617,1618,891);

delete from pqfquestions where questionID in (127,126,889,1519,872,1522,1617,1618,891);


update pqfdata 
	set answer = verifiedAnswer
	where verifiedAnswer is not null and verifiedAnswer <> '';

alter table pqfdata 
	drop column verifiedAnswer, 
	drop column isCorrect;


/**
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