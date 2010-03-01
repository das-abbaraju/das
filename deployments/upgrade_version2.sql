/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
where os.applicable = 1
and pcd.applies = 'No'
and pcd.catID = 151; 

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
join contractor_audit ca on ca.id = os.auditid 
where os.applicable = 0
and pcd.applies = 'Yes'
and pcd.catID = 151;
**/


ALTER TABLE `accounts` 
	ADD COLUMN `status` varchar(15)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'Pending' after `updateDate`, 
	DROP COLUMN `seesAll_B`, 
	DROP COLUMN `sendActivationEmail_B`, 
	DROP COLUMN `activationEmails_B`;

CREATE TABLE `app_page_logger`(
	`id` bigint(10) unsigned NOT NULL  auto_increment , 
	`startTime` datetime NOT NULL  , 
	`endTime` datetime NULL  , 
	`userID` mediumint(9) NULL  , 
	`pageName` varchar(100) COLLATE utf8_general_ci NOT NULL  , 
	`url` varchar(1000) COLLATE utf8_general_ci NOT NULL  , 
	PRIMARY KEY (`id`) , 
	KEY `pageStart`(`pageName`,`startTime`) , 
	KEY `userID`(`userID`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

ALTER TABLE `email_template` 
	ADD COLUMN `recipient` varchar(10)  COLLATE latin1_swedish_ci NULL after `html`, COMMENT='';

CREATE TABLE `flag_criteria_contractor`(
	`id` int(10) unsigned NOT NULL  auto_increment , 
	`createdBy` int(11) NULL  , 
	`updatedBy` int(11) NULL  , 
	`creationDate` datetime NULL  , 
	`updateDate` datetime NULL  , 
	`conID` mediumint(9) unsigned NOT NULL  , 
	`criteriaID` int(10) unsigned NOT NULL  , 
	`answer` varchar(100) COLLATE utf8_general_ci NOT NULL  , 
	`verified` tinyint(4) NOT NULL  DEFAULT '0' , 
	`answer2` varchar(100) COLLATE utf8_general_ci NULL  , 
	PRIMARY KEY (`id`) , 
	UNIQUE KEY `conID`(`conID`,`criteriaID`) , 
	KEY `criteriaID`(`criteriaID`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

CREATE TABLE `flag_criteria_operator`(
	`id` int(10) unsigned NOT NULL  auto_increment , 
	`createdBy` int(11) NULL  , 
	`updatedBy` int(11) NULL  , 
	`creationDate` datetime NULL  , 
	`updateDate` datetime NULL  , 
	`opID` mediumint(9) unsigned NOT NULL  , 
	`criteriaID` int(10) unsigned NOT NULL  , 
	`flag` enum('Red','Amber','Green') COLLATE utf8_general_ci NOT NULL  , 
	`hurdle` varchar(100) COLLATE utf8_general_ci NULL  , 
	`percentAffected` decimal(5,1) unsigned NULL  , 
	`lastCalculated` datetime NULL  , 
	`minRiskLevel` tinyint(4) unsigned NOT NULL  DEFAULT '0' , 
	PRIMARY KEY (`id`) , 
	UNIQUE KEY `opID`(`opID`,`criteriaID`,`flag`) , 
	KEY `criteriaID`(`criteriaID`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

CREATE TABLE `flag_data`(
	`id` int(10) unsigned NOT NULL  auto_increment , 
	`createdBy` int(11) NULL  , 
	`updatedBy` int(11) NULL  , 
	`creationDate` datetime NULL  , 
	`updateDate` datetime NULL  , 
	`conID` mediumint(9) unsigned NOT NULL  , 
	`opID` mediumint(9) unsigned NOT NULL  , 
	`criteriaID` int(10) unsigned NOT NULL  , 
	`flag` enum('Red','Amber','Green') COLLATE utf8_general_ci NULL  , 
	PRIMARY KEY (`id`) , 
	UNIQUE KEY `conID`(`conID`,`opID`,`criteriaID`) , 
	KEY `opID`(`opID`,`criteriaID`) , 
	KEY `criteriaID`(`criteriaID`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

CREATE TABLE `flag_data_override`(
	`id` int(10) unsigned NOT NULL  auto_increment , 
	`conID` mediumint(9) unsigned NOT NULL  , 
	`opID` mediumint(9) unsigned NOT NULL  , 
	`criteriaID` int(10) unsigned NOT NULL  , 
	`forceFlag` enum('Red','Amber','Green') COLLATE utf8_general_ci NOT NULL  , 
	`forceEnd` date NULL  , 
	`createdBy` int(11) NULL  , 
	`updatedBy` int(11) NULL  , 
	`creationDate` datetime NULL  , 
	`updateDate` datetime NULL  , 
	PRIMARY KEY (`id`) , 
	UNIQUE KEY `conID`(`conID`,`opID`,`criteriaID`) , 
	KEY `opID`(`opID`,`criteriaID`) , 
	KEY `criteriaID`(`criteriaID`) 
) ENGINE=InnoDB DEFAULT CHARSET='utf8';

ALTER TABLE `generalcontractors` 
	CHANGE `id` `id` int(8) unsigned   NOT NULL auto_increment first, 
	ADD COLUMN `flag` enum('Red','Amber','Green')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'Red' after `workStatus`, 
	ADD COLUMN `waitingOn` tinyint(4)   NOT NULL DEFAULT '0' after `flag`, 
	ADD COLUMN `relationshipType` varchar(10)  COLLATE latin1_swedish_ci NULL after `forceEnd`, 
	CHANGE `processCompletion` `processCompletion` datetime   NULL after `relationshipType`, 
	ADD COLUMN `flagLastUpdated` datetime   NULL after `processCompletion`, 
	DROP COLUMN `forceBegin`;

ALTER TABLE `operators` 
	DROP COLUMN `activationEmails`, 
	DROP COLUMN `doSendActivationEmail`;

ALTER TABLE `pqfquestions` 
	DROP COLUMN `question`, 
	DROP COLUMN `requirement`; 


update accounts set status = 'Active';
update accounts set status = 'Pending' WHERE active = 'N';
update accounts set status = 'Demo' where name like '%^^^%' or name like 'PICS%demo%';
update accounts set status = 'Deleted' where status != 'Active' and name like '%duplicat%';
update accounts set status = 'Deactivated' where status = 'Pending' and type = 'Contractor' and id in (select id from invoice where tableType = 'I' and status = 'Paid');

-- Changing Cron Statistics widget title to System Status
update widget set caption = 'System Status' where widgetID = 16;


/**  insert data into the flag_criteria_operator **/
-- insert data for PQF and Annual Updates
insert into flag_criteria_operator
select null,ao.createdBy, ao.updatedBy, ao.creationDate, ao.updateDate, ao.opid, f.id, 
ao.requiredForFlag,null as hurdle, null as percentAffected, null as lastcalculated,ao.minRiskLevel from audit_operator ao
join flag_criteria f on ao.auditTypeid = f.audittypeid
join audit_type at on at.id = ao.audittypeid
where
(ao.auditTypeid in (1,11) AND 
	(
		(ao.requiredAuditStatus = 'Active' and f.validationRequired = 1) 
		or
		(ao.requiredAuditStatus = 'Submitted' and f.validationRequired = 0)
	)
)
and ao.canSee = 1;

-- insert data for non PQF and Annual Updates
insert into flag_criteria_operator
select null,ao.createdBy, ao.updatedBy, ao.creationDate, ao.updateDate, ao.opid, f.id, 
ao.requiredForFlag,null as hurdle, null as percentAffected, null as lastcalculated,ao.minRiskLevel from audit_operator ao
join flag_criteria f on ao.auditTypeid = f.audittypeid
join audit_type at on at.id = ao.audittypeid
where ao.auditTypeid not in (1,11)
and ao.canSee = 1 
and ao.minriskLevel > 0;

update flag_criteria_operator set flag = 'Red' where flag = '';

-- updating the flag_criteria_opertor for Non-EMR questions 

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
where fo.questionid not in (2034);

-- updating the flag_criteria_opertor for EMR questions 
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
and fo.multiyearscope = f.multiyearscope
where fo.questionid in (2034);



-- updating the multiyearscope for AllThreeYears
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
and fo.multiyearscope = 'AllThreeYears'
and f.multiyearscope = 'LastYearOnly'
where fo.questionid in (2034);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
and fo.multiyearscope = 'AllThreeYears'
and f.multiyearscope = 'TwoYearsAgo'
where fo.questionid in (2034);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,f.ID, 
fo.flagStatus,fo.value as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagcriteria fo
join flag_criteria f on fo.questionid = f.questionid
and fo.multiyearscope = 'AllThreeYears'
and f.multiyearscope = 'ThreeYearsAgo'
where fo.questionid in (2034);


update flag_criteria_operator fo,flag_criteria k set fo.hurdle = NULL
where fo.criteriaid = k.id
and k.allowCustomValue = 0
and k.questionid is not null
and fo.hurdle is not null;

-- inserting on flag_criteria_operator for osha LWCR
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.lwcrhurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.lwcrTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.lwcrTime = 2)
)
and 
(	(k.oshaRateType = 'LwcrAbsolute' and fo.lwcrhurdleType = 'Absolute')
	or
	(k.oshaRateType = 'LwcrNaics' and fo.lwcrhurdleType = 'NAICS')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.lwcrhurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.lwcrTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.lwcrTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.lwcrTime = 1)
)
and 
(	(k.oshaRateType = 'LwcrAbsolute' and fo.lwcrhurdleType = 'Absolute')
	or
	(k.oshaRateType = 'LwcrNaics' and fo.lwcrhurdleType = 'NAICS')
);

-- inserting on flag_criteria_operator for osha TRIR
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.trirhurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.trirTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.trirTime = 2)
)
and 
(	(k.oshaRateType = 'TrirAbsolute' and fo.trirhurdleType = 'Absolute')
	or
	(k.oshaRateType = 'TrirNaics' and fo.trirhurdleType = 'NAICS')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.trirhurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.trirTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.trirTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.trirTime = 1)
)
and 
(	(k.oshaRateType = 'TrirAbsolute' and fo.trirhurdleType = 'Absolute')
	or
	(k.oshaRateType = 'TrirNaics' and fo.trirhurdleType = 'NAICS')
);

-- inserting on flag_criteria_operator for osha Fatalities
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.fatalitieshurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.fatalitiesTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.fatalitiesTime = 2)
)
and 
(	(k.oshaRateType = 'Fatalities' and fo.fatalitieshurdleType = 'Absolute')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.fatalitieshurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.fatalitiesTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.fatalitiesTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.fatalitiesTime = 1)
)
and 
(	(k.oshaRateType = 'Fatalities' and fo.fatalitieshurdleType = 'Absolute')
);


-- inserting on flag_criteria_operator for osha SeverityRate
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.severityhurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.severityTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.severityTime = 2)
)
and 
(	(k.oshaRateType = 'SeverityRate' and fo.severityhurdleType = 'Absolute')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.severityhurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.severityTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.severityTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.severityTime = 1)
)
and 
(	(k.oshaRateType = 'SeverityRate' and fo.severityhurdleType = 'Absolute')
);


-- inserting on flag_criteria_operator for osha CAd7
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.cad7hurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.cad7Time = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.cad7Time = 2)
)
and 
(	(k.oshaRateType = 'Cad7' and fo.cad7hurdleType = 'Absolute')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.cad7hurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.cad7Time = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.cad7Time = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.cad7Time = 1)
)
and 
(	(k.oshaRateType = 'Cad7' and fo.cad7hurdleType = 'Absolute')
);


-- inserting on flag_criteria_operator for osha Neer
insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.neerhurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'ThreeYearAverage' and fo.neerTime = 3)
	or
	(k.multiyearScope = 'LastYearOnly' and fo.neerTime = 2)
)
and 
(	(k.oshaRateType = 'Neer' and fo.neerhurdleType = 'Absolute')
);

insert into flag_criteria_operator 
select null, fo.createdBy, fo.updatedBy, fo.creationDate, 
fo.updateDate,fo.opID,k.id,fo.flagStatus, 
fo.neerhurdle as hurdle, null as percentAffected, null as lastcalculated,1 as minRiskLevel
from flagoshacriteria fo
join operators o on fo.opid = o.id
join flag_criteria k on k.oshaType = o.oshaType
where  
(
	(k.multiyearScope = 'LastYearOnly' and fo.neerTime = 1)
	or
	(k.multiyearScope = 'TwoYearsAgo' and fo.neerTime = 1)
	or
	(k.multiyearScope = 'ThreeYearsAgo' and fo.neerTime = 1)
)
and 
(	(k.oshaRateType = 'Neer' and fo.neerhurdleType = 'Absolute')
);


update flag_criteria_operator set createdBy = 1
where createdBy is null;

update flag_criteria_operator set updatedBy = 1
where updatedBy is null;

update flag_criteria_operator set creationDate = Now()
where creationDate is null;

update flag_criteria_operator set updateDate = Now()
where updateDate is null;

select * from operators where inheritaudits is null;
select * from operators where inheritInsurance is null;
select * from operators where inheritFlagCriteria is null;
select * from operators where inheritInsuranceCriteria is null;

-- clean the flag_criteria_operator data to remove the unused criteria data for audits
delete from flag_criteria_operator
where opid not in (select distinct inheritaudits from operators)
and criteriaid in (select f.id from flag_criteria f
join audit_type at on f.audittypeid = at.id
where at.classType != 'Policy');

delete from flag_criteria_operator
where opid not in (select distinct inheritInsurance from operators)
and criteriaid in (select f.id from flag_criteria f
join audit_type at on f.audittypeid = at.id
where at.classType = 'Policy');

-- clean the flag_criteria_operator data to remove the unused criteria data for questions
delete from flag_criteria_operator
where opid not in (select distinct inheritFlagCriteria from operators)
and criteriaid in (select f.id from flag_criteria f
join pqfquestions p on f.questionID = p.id 
join pqfsubcategories ps on ps.id = p.subcategoryid
join pqfcategories pc on pc.id = ps.categoryid
join audit_type at on at.id = pc.audittypeid
where at.classType != 'Policy');

delete from flag_criteria_operator
where opid not in (select distinct inheritInsuranceCriteria from operators)
and criteriaid in (select f.id from flag_criteria f
join pqfquestions p on f.questionID = p.id 
join pqfsubcategories ps on ps.id = p.subcategoryid
join pqfcategories pc on pc.id = ps.categoryid
join audit_type at on at.id = pc.audittypeid
where at.classType = 'Policy');

-- clean the flag_criteria_operator data to remove the unused criteria data for osha
delete from flag_criteria_operator
where opid not in (select distinct inheritFlagCriteria from operators)
and criteriaid in (select f.id from flag_criteria f
where f.oshatype is not null
and f.questionid is null 
and f.audittypeID is null);

-- clean up the generalcontractors table dates
update generalcontractors set creationDate = now() where creationDate = '0000-00-00 00:00:00';

ALTER TABLE `audit_operator` 
	CHANGE `help` `help` varchar(1000)  COLLATE latin1_swedish_ci NULL after `minRiskLevel`, 
	DROP COLUMN `requiredForFlag`, 
	DROP COLUMN `requiredAuditStatus`;

ALTER TABLE `accounts` 
	DROP COLUMN `active`;

update generalcontractors, flags
set generalcontractors.flag = flags.flag, 
	generalcontractors.waitingOn = flags.waitingOn, 
	generalcontractors.flagLastUpdated = flags.lastUpdate
where generalcontractors.genID = flags.opID
and generalcontractors.subID = flags.conID;

-- MOVE tables into archive DATABASE
RENAME TABLE flags to old_flags;
RENAME TABLE flagcriteria to old_flagcriteria;
RENAME TABLE flagoshacriteria to old_flagoshacriteria;

DROP TABLE `pqfquestion_operator`;
DROP TABLE `temp_user`;
DROP TABLE `temp_user_duplicates`;



                                                                     
                                                                     
                                                                     
                                             
/* Script to Prune flag_archive Table */

-- Delete all data older than 3 years 
delete from flag_archive where creationDate < DATE_SUB(NOW(),INTERVAL 3 YEAR);

-- Need to run this only once
update flag_archive set creationDate = '2008-07-01'
where creationDate = '2008-07-02';

-- Need to run this only once
update flag_archive set creationDate = '2008-12-01'
where creationDate = '2008-12-11';

-- Deleting all data other than past 7 days,1st of every month

-- Need to Run this only once
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 16;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 28;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 228;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 769;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 784;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 950;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 969;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1031;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1039;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1068;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1191;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1192;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1193;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1194;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1195;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1196;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1197;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1200;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1201;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1202;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1203;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1204;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1205;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1206;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1251;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1306;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1458;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1459;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1460;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1461;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1462;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1463;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 1813;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2021;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2022;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2073;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2175;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2463;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2475;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2691;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2714;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2723;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2727;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2812;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2921;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 2949;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3534;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3544;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3637;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3638;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3691;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3716;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3848;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3849;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3850;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3851;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3852;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3853;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3854;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3855;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3856;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3857;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3858;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3859;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3860;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3861;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 3937;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4091;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4162;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4278;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4440;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4499;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4620;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4638;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4660;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4662;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4744;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4808;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4838;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 4840;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5006;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5007;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5008;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5147;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5148;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5247;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5301;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5450;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5465;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5829;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5830;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5831;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5832;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5833;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5834;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5835;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5836;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5837;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5838;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5839;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5840;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5841;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5842;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5843;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5844;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5845;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5846;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5847;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5848;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5849;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5850;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5851;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5852;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5853;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5854;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5855;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5856;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5857;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5858;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5859;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5860;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5861;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5862;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5863;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5864;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5865;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5866;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5867;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5868;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5869;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5870;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5871;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5872;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5873;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5874;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5875;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5876;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5877;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5878;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5879;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5880;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5881;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5882;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5883;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5884;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5885;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5886;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5887;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5888;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5889;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5890;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5891;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5892;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5893;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5894;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5895;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5896;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5897;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5898;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5899;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5900;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5901;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5902;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5903;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5904;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5905;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5906;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5907;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5908;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5909;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5910;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5911;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5912;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5913;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5915;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5916;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5917;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5918;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5919;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5920;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5921;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5922;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5923;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5924;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5925;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5926;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5927;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5928;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5929;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5930;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5931;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5932;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5933;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5934;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5935;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5936;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5937;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5938;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5939;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5940;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5941;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5942;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5943;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5944;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5945;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5946;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5947;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5948;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5949;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5950;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5951;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5952;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5953;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5954;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5955;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5956;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5957;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5958;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5959;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5960;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5961;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5962;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 5977;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6040;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6112;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6121;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6122;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6123;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6124;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6125;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6127;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6128;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6166;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6228;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6244;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6245;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6246;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6247;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6248;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6403;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6418;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6528;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 6887;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 7454;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 7457;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 7458;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 7460;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 7462;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 7464;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 7466;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 7727;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 7786;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 8281;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 8607;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 8701;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 8769;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9319;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9345;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9365;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9366;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9367;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9370;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9394;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9415;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9416;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9418;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9420;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9423;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9644;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9645;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9763;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9788;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9801;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9804;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9814;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9815;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9902;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9903;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9947;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9958;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9960;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9961;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9962;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9963;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9964;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9965;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9966;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9967;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9968;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9969;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9970;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9971;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9972;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9973;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9974;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9975;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9976;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9977;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9978;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9979;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9980;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9981;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9982;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9983;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9984;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9985;
delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1 AND opID = 9986;

delete from flag_archive 
where creationDate < DATE_SUB(NOW(), INTERVAL 17 WEEK)
and DAYOFMONTH(creationDate) != 1;

-- Deleting all data other than past 7 days, sundays and 1st of every month for 17 Weeks

-- Need to run this once
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 9000; 
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 8000; 
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 7000; 
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 6000; 
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 5000; 
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 4000; 
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 3000; 
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 2000; 
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 1000; 


delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
and opID > 0; 





