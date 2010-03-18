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

ALTER TABLE `accounts` 
	CHANGE `name` `name` varchar(50)  COLLATE latin1_swedish_ci NOT NULL after `type`, 
	CHANGE `country` `country` varchar(25)  COLLATE latin1_swedish_ci NULL after `zip`, 
	CHANGE `phone` `phone` varchar(30)  COLLATE latin1_swedish_ci NULL after `country`, 
	CHANGE `phone2` `phone2` varchar(35)  COLLATE latin1_swedish_ci NULL after `phone`, 
	CHANGE `fax` `fax` varchar(30)  COLLATE latin1_swedish_ci NULL after `phone2`, 
	CHANGE `contactID` `contactID` mediumint(9)   NULL after `fax`, 
	CHANGE `email` `email` varchar(50)  COLLATE latin1_swedish_ci NULL after `contactID`, 
	CHANGE `web_URL` `web_URL` varchar(50)  COLLATE latin1_swedish_ci NULL after `email`, 
	CHANGE `industry` `industry` varchar(50)  COLLATE latin1_swedish_ci NULL after `web_URL`, 
	CHANGE `naics` `naics` varchar(10)  COLLATE latin1_swedish_ci NOT NULL DEFAULT '0' after `industry`, 
	CHANGE `naicsValid` `naicsValid` tinyint(4)   NOT NULL DEFAULT '0' after `naics`, 
	CHANGE `dbaName` `dbaName` varchar(100)  COLLATE latin1_swedish_ci NULL after `naicsValid`, 
	CHANGE `nameIndex` `nameIndex` varchar(50)  COLLATE latin1_swedish_ci NULL after `dbaName`, 
	CHANGE `qbListID` `qbListID` varchar(25)  COLLATE latin1_swedish_ci NULL after `nameIndex`, 
	CHANGE `qbSync` `qbSync` tinyint(4)   NOT NULL DEFAULT '1' after `qbListID`, 
	CHANGE `reason` `reason` varchar(100)  COLLATE latin1_swedish_ci NULL after `qbSync`, COMMENT='';

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
	ADD COLUMN `recipient` varchar(10)  COLLATE latin1_swedish_ci NULL;

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

ALTER TABLE `generalcontractors` 
	CHANGE `creationDate` `creationDate` datetime   NULL after `createdBy`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `creationDate`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `updatedBy`;
	
ALTER TABLE `operators` 
	DROP COLUMN `activationEmails`, 
	DROP COLUMN `doSendActivationEmail`;

ALTER TABLE `pqfquestions` 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `number`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `createdBy`, 
	CHANGE `creationDate` `creationDate` datetime   NOT NULL after `updatedBy`, 
	CHANGE `updateDate` `updateDate` datetime   NOT NULL after `creationDate`, 
	CHANGE `effectiveDate` `effectiveDate` date   NOT NULL after `updateDate`, 
	CHANGE `expirationDate` `expirationDate` date   NOT NULL after `effectiveDate`, 
	CHANGE `hasRequirement` `hasRequirement` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `expirationDate`, 
	CHANGE `okAnswer` `okAnswer` varchar(50)  COLLATE latin1_swedish_ci NULL after `hasRequirement`, 
	CHANGE `isRequired` `isRequired` enum('No','Yes','Depends')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `okAnswer`, 
	CHANGE `isVisible` `isVisible` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `isRequired`, 
	CHANGE `dependsOnQID` `dependsOnQID` smallint(6)   NULL after `isVisible`, 
	CHANGE `dependsOnAnswer` `dependsOnAnswer` varchar(100)  COLLATE latin1_swedish_ci NULL after `dependsOnQID`, 
	CHANGE `questionType` `questionType` varchar(50)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'Text' after `dependsOnAnswer`, 
	CHANGE `title` `title` varchar(250)  COLLATE latin1_swedish_ci NULL after `questionType`, 
	CHANGE `columnHeader` `columnHeader` varchar(30)  COLLATE latin1_swedish_ci NULL after `title`, 
	CHANGE `isGroupedWithPrevious` `isGroupedWithPrevious` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `columnHeader`, 
	CHANGE `isRedFlagQuestion` `isRedFlagQuestion` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `isGroupedWithPrevious`, 
	CHANGE `link` `link` varchar(250)  COLLATE latin1_swedish_ci NULL after `isRedFlagQuestion`, 
	CHANGE `linkText` `linkText` varchar(250)  COLLATE latin1_swedish_ci NULL after `link`, 
	CHANGE `linkURL1` `linkURL1` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText`, 
	CHANGE `linkText1` `linkText1` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL1`, 
	CHANGE `linkURL2` `linkURL2` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText1`, 
	CHANGE `linkText2` `linkText2` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL2`, 
	CHANGE `linkURL3` `linkURL3` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText2`, 
	CHANGE `linkText3` `linkText3` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL3`, 
	CHANGE `linkURL4` `linkURL4` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText3`, 
	CHANGE `linkText4` `linkText4` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL4`, 
	CHANGE `linkURL5` `linkURL5` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText4`, 
	CHANGE `linkText5` `linkText5` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL5`, 
	CHANGE `linkURL6` `linkURL6` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkText5`, 
	CHANGE `linkText6` `linkText6` varchar(255)  COLLATE latin1_swedish_ci NULL after `linkURL6`, 
	CHANGE `uniqueCode` `uniqueCode` varchar(50)  COLLATE latin1_swedish_ci NULL after `linkText6`, 
	CHANGE `showComment` `showComment` tinyint(4)   NOT NULL DEFAULT '0' after `uniqueCode`, 
	CHANGE `riskLevel` `riskLevel` tinyint(4)   NULL after `showComment`, 
	CHANGE `helpPage` `helpPage` varchar(100)  COLLATE latin1_swedish_ci NULL after `riskLevel`, 
	CHANGE `countries` `countries` varchar(100)  COLLATE latin1_swedish_ci NULL after `helpPage`, 
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
AND ao.requiredForFlag != 'Green'
and ao.canSee = 1;

-- insert data for non PQF and Annual Updates
insert into flag_criteria_operator
select null,ao.createdBy, ao.updatedBy, ao.creationDate, ao.updateDate, ao.opid, f.id, 
ao.requiredForFlag,null as hurdle, null as percentAffected, null as lastcalculated,ao.minRiskLevel from audit_operator ao
join flag_criteria f on ao.auditTypeid = f.audittypeid
join audit_type at on at.id = ao.audittypeid
where ao.auditTypeid not in (1,11)
AND ao.requiredForFlag != 'Green'
and ao.canSee = 1;

delete from flag_criteria_operator where flag = '';

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

-- Update the flag_criteria_operator percentAffected to 0
update flag_criteria_operator 
set percentAffected = 0
where percentAffected is null;

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

ALTER TABLE `flag_criteria_operator` 
	ADD COLUMN `affected` smallint(4) unsigned   NOT NULL DEFAULT '0' after `minRiskLevel`;

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
drop table `temp_contractor_audit`;
drop table `temp_expired_audits`;
DROP TABLE `temp_user_duplicates`;
DROP TABLE `account_name`;

-- Deleting all data other than past 7 days, sundays and 1st of every month for 17 Weeks
-- Need to run this once
delete from flag_archive 
where creationDate < DATE_SUB(NOW(),INTERVAL 7 DAY)
and DAYOFMONTH(creationDate) != 1
and DAYOFWEEK(creationDate) != 1
limit 100000;

