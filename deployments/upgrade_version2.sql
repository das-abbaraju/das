ALTER TABLE `accounts` 
	CHANGE `naics` `naics` varchar(10)  COLLATE latin1_swedish_ci NOT NULL DEFAULT '0';

ALTER TABLE `audit_operator` 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `opID`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `createdBy`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `updatedBy`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `creationDate`, 
	CHANGE `canSee` `canSee` tinyint(3) unsigned   NOT NULL DEFAULT '1' after `updateDate`, 
	CHANGE `canEdit` `canEdit` tinyint(3) unsigned   NOT NULL DEFAULT '0' after `canSee`, 
	CHANGE `minRiskLevel` `minRiskLevel` tinyint(3) unsigned   NOT NULL after `canEdit`, 
	CHANGE `requiredForFlag` `requiredForFlag` varchar(10)  COLLATE latin1_swedish_ci NULL after `minRiskLevel`, 
	CHANGE `requiredAuditStatus` `requiredAuditStatus` varchar(15)  COLLATE latin1_swedish_ci NULL DEFAULT 'Active' after `requiredForFlag`, 
	ADD COLUMN `help` varchar(1000)  COLLATE latin1_swedish_ci NULL after `requiredAuditStatus`, 
	DROP COLUMN `orderedCount`, 
	DROP COLUMN `orderDate`, 
	DROP COLUMN `additionalInsuredFlag`, 
	DROP COLUMN `waiverSubFlag`, COMMENT='';

CREATE TABLE `certificate`(
	`id` int(10) unsigned NOT NULL  auto_increment , 
	`conID` int(10) unsigned NOT NULL  , 
	`fileType` varchar(4) COLLATE latin1_swedish_ci NOT NULL  , 
	`description` varchar(100) COLLATE latin1_swedish_ci NULL  , 
	`createdBy` int(11) NULL  , 
	`updatedBy` int(11) NULL  , 
	`creationDate` datetime NULL  , 
	`updateDate` datetime NULL  , 
	PRIMARY KEY (`id`) 
) ENGINE=MyISAM DEFAULT CHARSET='latin1';


ALTER TABLE `contractor_audit_operator` 
	CHANGE `createdBy` `createdBy` int(11)   NULL after `opID`, 
	CHANGE `updatedBy` `updatedBy` int(11)   NULL after `createdBy`, 
	CHANGE `creationDate` `creationDate` datetime   NULL after `updatedBy`, 
	CHANGE `updateDate` `updateDate` datetime   NULL after `creationDate`, 
	CHANGE `status` `status` varchar(30)  COLLATE latin1_swedish_ci NULL after `updateDate`, 
	ADD COLUMN `statusChangedBy` int(11)   NULL after `status`, 
	ADD COLUMN `visible` tinyint(4)   NOT NULL DEFAULT '1' after `statusChangedBy`, 
	ADD COLUMN `valid` tinyint(4)   NULL after `visible`, 
	ADD COLUMN `certificateID` int(11)   NULL after `valid`, 
	ADD COLUMN `flag` varchar(6)  COLLATE latin1_swedish_ci NULL after `certificateID`, 
	CHANGE `notes` `notes` varchar(200)  COLLATE latin1_swedish_ci NULL after `flag`, 
	ADD COLUMN `reason` varchar(200)  COLLATE latin1_swedish_ci NULL after `notes`, 
	ADD COLUMN `statusChangedDate` datetime   NULL after `reason`, 
	DROP COLUMN `inherit`, COMMENT='';

ALTER TABLE `operatorforms` 
	ADD COLUMN `formType` varchar(20)  COLLATE latin1_swedish_ci NULL after `updateDate`, COMMENT='';

	
/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/

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

/**
 * update the visible field to 0 on the CAO if not required
 */
update contractor_audit_operator cao set visible = 0 
where status = 'NotApplicable' 
and recommendedStatus = 'NotApplicable'
and creationDate = updateDate;

update contractor_audit_operator cao set flag = 'Green'
where recommendedStatus = 'Approved';

update contractor_audit_operator cao set flag = 'Red'
where recommendedStatus = 'Rejected';

ALTER TABLE `contractor_audit_operator` 
	DROP COLUMN `recommendedStatus`;

/**
 * update the column header for these questions on live 
**/
select pq.* from pqfquestions pq 
where subcategoryId in (select ps.id from pqfsubcategories ps where ps.subcategory = 'Policy Limits')
and columnHeader = '';

/**
 * set the helpText to NULL for all operator requirements
 **/ 
update pqfsubcategories set helpText = NULL
where subcategory = 'Operator Requirements';

update pqfquestions set isVisible = 'No'
where id in (2099,2100,2201,2387,2198,2199,2205,2119,2207,2125,2131,2213,2137,2216,2143,2219,2237,2283,2291,2393,2397);

update pqfquestions set minimumTuples = 0 WHERE uniqueCode = 'aiName';

update operatorforms set formType = 'Insurance'
where formName like '%Insurance%';

update contractor_audit_operator set statusChangedBy = updatedBy,
statusChangedDate = updateDate;

update pqfquestions set expirationDate = '2009-01-01' where question = 'Policy Number';

update pqfquestions set 
	question = 'Insurance Carrier', 
	updatedBy = 941, 
	updateDate = NOW(), 
	effectiveDate = '2008-01-01', 
	questionType = 'AMBest', 
	columnHeader = 'Carrier', 
	isRedFlagQuestion = 'Yes', 
	uniqueCode = 'carrier'
where question like 'Insurance Carrier NAIC%';

update pqfquestions set 
	updatedBy = 941, 
	updateDate = NOW(),
	isVisible = 'No',
	update pqfquestions set 
	question = 'Insurance Carrier', 
	updatedBy = 941, 
	updateDate = NOW(), 
	effectiveDate = '2008-01-01', 
	questionType = 'AMBest', 
	columnHeader = 'Carrier', 
	isRedFlagQuestion = 'Yes', 
	uniqueCode = 'carrier'
where question like 'Insurance Carrier NAIC%';

update pqfquestions set 
	updatedBy = 941, 
	updateDate = NOW(),
	uniqueCode = 'carrierName',
	isVisible = 'No'
where question like 'Insurance Carrier Name%';

drop table ambest_conversion;
create table ambest_conversion as
select ca.id auditID, d1.id data1ID, d1.answer carrier_naic, d1.questionID, d2.id data2ID, d2.answer carrier_name, d2.questionID
from contractor_audit ca
join audit_type t on ca.auditTypeID = t.id and t.classType = 'Policy'
LEFT JOIN pqfdata d1 on ca.id = d1.auditID AND d1.questionID IN (SELECT id FROM pqfquestions WHERE uniqueCode = 'carrier')
LEFT join pqfdata d2 on ca.id = d2.auditID AND d2.questionID IN (SELECT id FROM pqfquestions WHERE uniqueCode = 'carrierName')
limit 100

delete from ambest_conversion where carrier_naic is null and carrier_name is null;

update pqfdata d, ambest_conversion c
set d.comment = c.carrier_naic,
d.answer = c.carrier_name
where d.id = c.data1ID and c.data2ID > 0;

-- TODO handle these too
select * from ambest_conversion
where carrier_naic is null and carrier_name is not null
