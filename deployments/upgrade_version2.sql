USE `pics_stage`;

ALTER TABLE `accounts` 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `name`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `createdBy`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `creationDate`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `updatedBy`, 
	CHANGE `active` `active` char(1)  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'N' after `updateDate`, 
	CHANGE `lastLogin` `lastLogin` datetime   NULL after `active`, 
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
	ADD COLUMN `naics` varchar(10)  COLLATE latin1_swedish_ci NULL after `industry`, 
	CHANGE `nameIndex` `nameIndex` varchar(50)  COLLATE latin1_swedish_ci NULL after `naics`, 
	CHANGE `seesAll_B` `seesAll_B` char(1)  COLLATE latin1_swedish_ci NULL DEFAULT 'N' after `nameIndex`, 
	CHANGE `sendActivationEmail_B` `sendActivationEmail_B` char(1)  COLLATE latin1_swedish_ci NULL DEFAULT 'N' after `seesAll_B`, 
	CHANGE `activationEmails_B` `activationEmails_B` varchar(155)  COLLATE latin1_swedish_ci NULL after `sendActivationEmail_B`, 
	CHANGE `qbSync` `qbSync` tinyint(4)   NOT NULL DEFAULT '1' after `activationEmails_B`, 
	CHANGE `qbListID` `qbListID` varchar(25)  COLLATE latin1_swedish_ci NULL after `qbSync`, 
	DROP COLUMN `synced`, 
	DROP KEY `synced`, add KEY `synced`(`qbSync`), COMMENT='';

ALTER TABLE `flagoshacriteria` 
	ADD COLUMN `lwcrHurdleType` varchar(15)  COLLATE latin1_swedish_ci NULL after `flagLwcr`, 
	CHANGE `lwcrHurdle` `lwcrHurdle` decimal(5,2)   NOT NULL after `lwcrHurdleType`, 
	CHANGE `lwcrTime` `lwcrTime` tinyint(3) unsigned   NOT NULL DEFAULT '1' after `lwcrHurdle`, 
	CHANGE `flagTrir` `flagTrir` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `lwcrTime`, 
	ADD COLUMN `trirHurdleType` varchar(15)  COLLATE latin1_swedish_ci NULL after `flagTrir`, 
	CHANGE `trirHurdle` `trirHurdle` decimal(5,2)   NOT NULL after `trirHurdleType`, 
	CHANGE `trirTime` `trirTime` tinyint(3) unsigned   NOT NULL DEFAULT '1' after `trirHurdle`, 
	CHANGE `flagFatalities` `flagFatalities` enum('No','Yes')  COLLATE latin1_swedish_ci NOT NULL DEFAULT 'No' after `trirTime`, 
	ADD COLUMN `fatalitiesHurdleType` varchar(15)  COLLATE latin1_swedish_ci NULL after `flagFatalities`, 
	CHANGE `fatalitiesHurdle` `fatalitiesHurdle` decimal(5,2)   NOT NULL after `fatalitiesHurdleType`, 
	CHANGE `fatalitiesTime` `fatalitiesTime` tinyint(3) unsigned   NOT NULL DEFAULT '1' after `fatalitiesHurdle`, 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `fatalitiesTime`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `createdBy`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `updatedBy`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `creationDate`, COMMENT='';

ALTER TABLE `operators` 
	ADD COLUMN `oshaType` varchar(10)  COLLATE latin1_swedish_ci NULL DEFAULT 'OSHA' after `inheritInsurance`, COMMENT='';

ALTER TABLE `note` 
	ADD KEY `createdByStatus`(`createdBy`,`status`), COMMENT='';



/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/
UPDATE audit_type set classType = 'PQF'
where auditName like 'PQF%';

update flagoshacriteria set lwcrHurdleType = 'None', trirHurdleType = 'None', fatalitiesHurdleType = 'None';

update flagoshacriteria set lwcrHurdleType = 'Absolute' where flagLwcr = 'Yes';
update flagoshacriteria set trirHurdleType = 'Absolute' where flagTrir = 'Yes';
update flagoshacriteria set fatalitiesHurdleType = 'Absolute' where flagFatalities = 'Yes';

/**
 * update the NAICS from the pqfdata for the existing contractors
 * 
 */

update accounts a, (
select ca.conID, answer from contractor_audit ca
join pqfdata pd on pd.auditID = ca.id
join naics n on n.code = pd.answer 
where ca.auditTypeID = 1
and pd.questionid = 57
) t
set a.naics = t.answer
where a.id = t.conID;

/**
 * Added open Notes widget to all users
 */
insert into widget values
('Open Notes', 'Html',0,'UserOpenNotesAjax.action',null, null);

insert into widget_user values
(newwidgetid, 941, 1,1,5, null);
insert into widget_user values
(newwidgetid, 910, 1,1,5, null);
insert into widget_user values
(newwidgetid, 616, 1,1,5, null);
insert into widget_user values
(newwidgetid, 646, 1,1,5, null);

/**
 * Splitting up the pqf operator specific categories into their audits 
 * 
 */
select * from temp_category_audittype;

-- update the categories to the new auditTypeID 
update pqfcategories pc, temp_category_audittype tca set pc.auditTypeID = tca.auditTypeID
where pc.id = tca.categoryID
and pc.auditTypeID = 1;  

-- delete from contractor_audit any audits with the new auditTypeID
delete from contractor_audit  
where auditTypeID IN (select tca.auditTypeID from temp_category_audittype tca);

-- Create audits for these new Categories
insert into contractor_audit
select null, tca.auditTypeID, canew.conID, canew.creationDate,canew.auditStatus,
  canew.expiresDate,canew.auditorID,canew.assignedDate,canew.scheduledDate,
  canew.completedDate,canew.closedDate,canew.requestedByOpID,
  canew.auditLocation,canew.percentComplete,0,
  canew.contractorConfirm,canew.auditorConfirm,canew.manuallyAdded,
  canew.auditFor,canew.createdBy,canew.updatedBy,NOW(),canew.score,
  null from contractor_audit canew
join pqfcatdata pcd on pcd.auditid = canew.id
join temp_category_audittype tca on tca.categoryID = pcd.catID
where canew.auditTypeID = 1
AND pcd.applies = 'Yes';

-- insert into temp_auditconverison the old auditid, new auditid and catid
insert into temp_auditconversion
select null, caold.id,canew.id, pcd.catid from contractor_audit canew
join contractor_audit caold on canew.conid = caold.conid
join audit_type at on at.id = canew.auditTypeID
join pqfcatdata pcd on pcd.auditid = caold.id
join temp_category_audittype tca on tca.categoryID = pcd.catID
where caold.auditTypeID = 1
and at.id = tca.auditTypeID
and pcd.applies = 'Yes';

-- Also update the new auditid on the pqfcatData
update pqfcatdata pcd, temp_auditconversion tca set pcd.auditID = tca.customID
where pcd.auditID = tca.pqfID
and pcd.applies = 'Yes'
and pcd.catId = tca.catID;

-- update the pqfdata with the new audits
update pqfdata pd, temp_auditconversion tca, pqfquestions pqf, pqfsubcategories ps, 
pqfcategories pc, pqfcatdata pcd  
set pd.auditid = tca.customID
where pd.auditid = tca.pqfID
and pd.questionid = pqf.id
and ps.id = pqf.subcategoryID
and pc.id = ps.categoryID
and pc.id = tca.catID
and pcd.auditid = tca.customid
and pcd.applies = 'Yes'
and pcd.catid = pc.id;


-- Remove all the other categories on the pqfcatData where applies = 'No'
select count(*) from contractor_audit ca
join pqfcatdata pcd on ca.id = pcd.auditid
join temp_category_audittype tca on pcd.catid = tca.categoryID
where ca.audittypeId = 1;
 
delete from pqfcatdata 
where auditid in (select ca.id from contractor_audit ca where ca.audittypeId = 1)
and catid in (select tca.categoryID from temp_category_audittype tca);

-- update the new audits with percent Complete and auditStatus
update contractor_audit ca,temp_category_audittype tca, pqfcatdata pcd 
set ca.auditstatus = 'Pending', ca.percentComplete = pcd.percentCompleted
where tca.audittypeID = ca.auditTypeid
and ca.id = pcd.auditid
and pcd.percentCompleted < 100;

update contractor_audit ca,temp_category_audittype tca, pqfcatdata pcd 
set ca.auditstatus = 'Active', ca.percentComplete = pcd.percentCompleted
where tca.audittypeID = ca.auditTypeid
and ca.id = pcd.auditid
and pcd.percentCompleted = 100;
 
-- update it to audit_operator matrix for these operators looking at the pqf of this operator.
insert into audit_operator 
select null, tca.auditTypeid, ao.opID, ao.canSee, ao.canEdit, 
	min(pm.risklevel), ao.orderedCount,ao.orderDate, 
	ao.requiredForFlag, 
	ao.requiredAuditStatus, 
	ao.additionalInsuredFlag, 
	ao.waiverSubFlag, 
	ao.createdBy, 
	ao.updatedBy, 
	ao.creationDate, 
	Now()
from audit_operator ao 
join pqfopmatrix pm on ao.opID = pm.opID
join temp_category_audittype tca on tca.categoryid = pm.catid
where ao.auditTypeid = 1
group by pm.catid, pm.opid; 

-- Remove these categories from pqfopmatrix
delete from pqfopmatrix where catid in (select tca.categoryID from temp_category_audittype tca);

update operators set inheritFlagCriteria = id where inheritFlagCriteria is null;
update operators set inheritInsuranceCriteria = id where inheritInsuranceCriteria is null;
update operators set inheritAudits = id where inheritAudits is null;
update operators set inheritAuditCategories = id where inheritAuditCategories is null;
update operators set inheritInsurance = id where inheritInsurance is null;

delete from flagcriteria where isChecked = 'No';
alter table flagcriteria drop column isChecked;

update flagoshacriteria
set createdBy = 1, updatedBy = 1, creationDate = '2000-01-01', updateDate = now();


-- Remove the isApplicable field from the osha table
update pqfcatdata pcd, osha_audit os, contractor_audit ca set pcd.applies = 'No'
where pcd.auditid = os.auditid
and ca.id = os.auditid
and os.applicable = 0
and pcd.applies = 'Yes'
and pcd.catID = 151
and os.manhours = 0
and os.fileuploaded = 0
and ca.auditSTatus IN ('Active','Submitted');

create TEMPORARY table temp_oshaaudit
(id Mediumint(8));

insert into temp_oshaaudit
select os.id from osha_audit os
join pqfcatdata pcd on pcd.auditid = os.auditid
where os.applicable = 1
and pcd.applies = 'No'
and pcd.catID = 151
and os.manhours = 0
and os.fileuploaded = 0;

delete from osha_audit where id in (select id from temp_oshaaudit);

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

