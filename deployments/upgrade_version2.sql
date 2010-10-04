-- move app_index AND app_index_stats
-- move app_translation
-- move audit_category, audit_question
-- move audit_category_rule, audit_type_rule
-- move workflow, workflow_step


/** Update the requiresOQ for all contractors
 * we don't want to run this yet 
update accounts set requiresOQ = 1
where id in (select distinct conid from contractor_audit ca
join pqfdata pd on ca.id = pd.auditid
where pd.questionid = 894
and pd.answer = 'Yes');
**/

-- PICS-813: Corporate Email Subscriptions
update email_template set body = '<SubscriptionHeader>
Below are all contractors who have recently selected#if( ${operators.size()} > 1 ) one of#end your facilit#if( ${operators.size()} > 1 )ies 
#else
y#end as a work site.
<br/><br/>

#foreach( $operator in $operators.keySet() )
#if( $operators.get($operator).size() > 0 )
<strong>Facility: <em>${operator.name}</em></strong><br/>
<table style="border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;">
 <thead>
  <tr style="vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;">
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Contractor Name</td>
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Date Added</td>
  </tr>
 </thead>
 <tbody>
  #foreach( $contractor in $operators.get($operator).keySet() )
  <tr style="margin:0px">
   <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;"><a href="http://www.picsorganizer.com/ContractorView.action?id=$contractor.id#if( ${operators.size()} > 1 )&opID=${operator.id}#end">$contractor.name</a></td>
   <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;">$pics_dateTool.format("MM/dd/yy HH:mm", $operators.get($operator).get($contractor).creationDate)</td>
  </tr>
  #end
 </tbody>
</table>
<br/><br/>
#end
#end

<TimeStampDisclaimer>

<SubscriptionFooter>' where id = 107;

update email_template set body = '<SubscriptionHeader>

Below are all contractors who have recently registered with PICS and selected#if( ${operators.size()} > 1 ) one of#end your facilit#if( ${operators.size()} > 1 )ies 
#else
y#end as a work site.
<br/><br/>

#foreach( $operator in $operators.keySet() )
#if( $operators.get($operator).size() > 0 )
<strong>Facility: <em>${operator.name}</em></strong><br/>
<table style="border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;">
 <thead>
  <tr style="vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;">
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Contractor Name</td>
   <td style="border: 1px solid #e0e0e0; padding: 4px;">Registration</td>
  </tr>
 </thead>
 <tbody>
  #foreach( $contractor in $operators.get($operator) )
  <tr style="margin:0px">
   <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;"><a href="http://www.picsorganizer.com/ContractorView.action?id=${contractor.id}">$contractor.name</a></td>
   <td style="border: 1px solid #A84D10; padding: 4px; font-size: 13px;">$pics_dateTool.format("MM/dd/yy HH:mm", $contractor.creationDate)</td>
  </tr>
  #end
 </tbody>
</table>
<br/><br/>
#end
#end

<TimeStampDisclaimer>

<SubscriptionFooter>' where id = 62;
-- END:PICS-813

-- PICS-595: Waiting On
insert into widget_user
(id, widgetID, userID, expanded, widget_user.column, sortOrder, customConfig)
values
(null, 33, 959, 1, 2, 37, null);

-- PICS-415
update `token` set `velocityCode`='${flagColor}' where `tokenID`='8';

-- PICS-630/805 --
update accounts set nameIndex = replace(nameIndex, ' ', '');

-- PICS-788, PICS-42: Update TRIR report to Incidence Report (Graph), change permission on Incidence Rate Report
insert into useraccess 
(accessID, userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
select null, ua.userID, 'TRIRReport', ua.viewFlag, ua.editFlag, ua.deleteFlag, ua.grantFlag, now(), grantedByID
from useraccess ua where ua.accessType = 'FatalitiesReport';

-- Update existing accounts (operator, contractor) to use onsite services
update accounts
set onsiteServices = 1
where type in ('Contractor', 'Operator');

update audit_type set workflowID = 1;
update audit_type set workflowID = 2 where hasRequirements = 1;
update audit_type set workflowID = 3 where classType = 'Policy';
update audit_type set workflowID = 4 where id = 1;
update audit_type set workflowID = 5 where id = 11;



-- START STILL TODO
-- insert the Audit Category Data for subcategories
select null,acd.auditID,acs.id,acd.requiredCompleted,acd.numRequired,acd.numAnswered, 
acd.applies,acd.percentCompleted,acd.percentVerified,acd.percentClosed,acd.override, 
acd.score,acd.scoreCount,acd.createdBy,acd.updatedBy,acd.creationDate,acd.updateDate
from audit_category acp
join audit_category acs on acp.id = acs.parentID
join audit_cat_data acd on acd.categoryID = acp.id
where acp.parentID is null;

insert into pqfdata 
(auditID, questionID, answer, dateVerified, auditorID, createdBy, creationDate, updatedBy, updateDate)
select cao.auditID, q.id, cao.valid, cao.statusChangedDate, cao.statusChangedBy, cao.createdBy, cao.creationDate, cao.updatedBy, cao.updateDate
from contractor_audit_operator cao
join contractor_audit ca on ca.id = cao.auditID
join audit_category ac on ac.auditTypeID = ca.auditTypeID
join temp_cao_conversion t on t.auditTypeID = ca.auditTypeID and t.opID = cao.opID and ac.legacyID = t.id
join audit_question q on q.categoryID = ac.id and q.number = 2
where visible = 1 and valid > '';

insert into pqfdata 
(auditID, questionID, answer, dateVerified, auditorID, createdBy, creationDate, updatedBy, updateDate)
select cao.auditID, q.id, certificateID, cao.statusChangedDate, cao.statusChangedBy, cao.createdBy, cao.creationDate, cao.updatedBy, cao.updateDate
from contractor_audit_operator cao
join contractor_audit ca on ca.id = cao.auditID
join audit_category ac on ac.auditTypeID = ca.auditTypeID
join temp_cao_conversion t on t.auditTypeID = ca.auditTypeID and t.opID = cao.opID and ac.legacyID = t.id
join audit_question q on q.categoryID = ac.id and q.number = 1
where visible = 1 and cao.certificateID > 0;

-- Conversion for Policy CAOs
update contractor_audit_operator 
set status = 'Incomplete'
where status = 'Rejected';

update contractor_audit_operator 
set status = 'Complete'
where status = 'Verified';

-- 
insert into facilities (corporateID, opID) select 5, id from accounts where country = 'US' and type = 'Operator';
insert into facilities (corporateID, opID) select 6, id from accounts where country = 'CA' and type = 'Operator';

/*
 * BEGIN: CAO Conversion
 */
TRUNCATE TABLE temp_cao;
INSERT INTO temp_cao 
SELECT NULL as id,
  0                  include,
  c.id               conID,
  o.id               opID,
  o.inheritAudits    gbid,
  ca.auditTypeID,
  ca.id              auditID,
  ca.auditStatus
FROM contractor_info c
JOIN contractor_audit ca ON ca.conid = c.id
JOIN generalcontractors gc ON gc.subid = c.id
JOIN operators o ON o.id = gc.genid
JOIN accounts a ON a.id = gc.genid and a.type = 'Operator'
WHERE auditTypeID NOT IN (SELECT id FROM audit_type WHERE classtype = 'Policy')
-- and c.id < 100
;

-- Generate update statements for each rule
SELECT DISTINCT
concat('UPDATE temp_cao SET include = ', include, ifnull(concat(' WHERE gbID = ',opID), ''), ifnull(concat( CASE when opID is null then ' WHERE' else ' AND' end, ' auditTypeID = ', auditTypeID), ''), ';' ) 
FROM audit_type_rule ORDER BY priority;

insert into contractor_audit_operator (auditID, opID, status, submittedDate, completedDate, visible, createdBy, updatedBy, creationDate, updateDate)
select distinct
 ca.id            auditID,
 t.gbID           opID,
 ca.auditStatus   status,
 ca.completedDate submittedDate,
 ca.closedDate    completedDate,
 1                visible
 createdBy, updatedBy, creationDate, updateDate
from temp_cao t
join contractor_audit ca on t.auditID = ca.id
where t.auditTypeID = 1;

insert into contractor_audit_operator_permission (caoID, opID)
select cao.id, t.opID from contractor_audit_operator cao
join temp_cao t on cao.auditID = t.auditID and cao.opID = t.gbID and t.auditTypeID = 1;

insert into contractor_audit_operator (auditID, opID, status, submittedDate, completedDate, visible, createdBy, updatedBy, creationDate, updateDate)
select distinct
 ca.id            auditID,
 6                opID, -- PICS Canada
 ca.auditStatus   status,
 ca.completedDate submittedDate,
 ca.closedDate    completedDate,
 1                visible
 ca.createdBy, ca.updatedBy, ca.creationDate, ca.updateDate
from temp_cao t
join contractor_audit ca on t.auditID = ca.id
join accounts o on t.opID = o.id
where t.auditTypeID = 11
and o.country = 'CA';

insert into contractor_audit_operator_permission (caoID, opID)
select cao.id, t.opID from contractor_audit_operator cao
join temp_cao t on cao.auditID = t.auditID and t.auditTypeID = 11
join accounts o on t.opID = o.id AND o.country = 'CA'
where cao.opID = 6;

insert into contractor_audit_operator (auditID, opID, status, submittedDate, completedDate, visible, createdBy, updatedBy, creationDate, updateDate)
select distinct
 ca.id            auditID,
 5                opID, -- PICS US
 ca.auditStatus   status,
 ca.completedDate submittedDate,
 ca.closedDate    completedDate,
 1                visible
 ca.createdBy, ca.updatedBy, ca.creationDate, ca.updateDate
from temp_cao t
join contractor_audit ca on t.auditID = ca.id
join accounts o on t.opID = o.id
where t.auditTypeID = 11
and o.country = 'US';

insert into contractor_audit_operator_permission (caoID, opID)
select cao.id, t.opID from contractor_audit_operator cao
join temp_cao t on cao.auditID = t.auditID and t.auditTypeID = 11
join accounts o on t.opID = o.id AND o.country = 'US'
where cao.opID = 5;

insert into contractor_audit_operator (auditID, opID, status, submittedDate, completedDate, visible, createdBy, updatedBy, creationDate, updateDate)
select distinct
 ca.id            auditID,
 4                opID, -- PICS Global
 ca.auditStatus   status,
 ca.completedDate submittedDate,
 ca.closedDate    completedDate,
 1                visible
 ca.createdBy, ca.updatedBy, ca.creationDate, ca.updateDate
from temp_cao t
join contractor_audit ca on t.auditID = ca.id
join accounts o on t.opID = o.id
where t.auditTypeID not IN (1,11)
and include = 1;

insert into contractor_audit_operator_permission (caoID, opID)
select cao.id, t.opID from contractor_audit_operator cao
join temp_cao t on cao.auditID = t.auditID and t.auditTypeID NOT IN (1,11) and include = 1;

update contractor_audit_operator cao, contractor_audit ca set cao.statusChangedDate = ca.closedDate where cao.auditID = ca.id and cao.statusChangedDate is null and cao.status IN ('Complete','Active','Approved');
update contractor_audit_operator set statusChangedDate = updateDate where statusChangedDate IS NULL;

update contractor_audit_operator set status = 'Complete' where status IN ('Active');


/*
 * END: CAO Conversion
 */

-- For Policies 
update flag_criteria fc
join audit_type at on at.id = fc.auditTypeID
set fc.requiredStatus = 'Submitted'
where at.classType = 'Policy'
and validationRequired = 0;

update flag_criteria fc
join audit_type at on at.id = fc.auditTypeID
set fc.requiredStatus = 'Approved'
where at.classType = 'Policy'
and validationRequired = 1;

-- For Non Policies 
update flag_criteria fc
join audit_type at on at.id = fc.auditTypeID
set fc.requiredStatus = 'Submitted'
where at.classType != 'Policy'
and validationRequired = 0;

update flag_criteria fc
join audit_type at on at.id = fc.auditTypeID
set fc.requiredStatus = 'Complete'
where at.classType != 'Policy'
and validationRequired = 1;

delete from audit_category
where id in (select cid from temp_single_subcats);

drop table temp_single_subcats;


/*  DDL Changes
 *  Dropping Tables and Columns
 */
alter table contractor_audit_operator drop column `submittedDate`, drop column `completedDate`, drop column `approvedDate`, drop column `incompleteDate`, drop column `notes`, drop column `reason`, drop column `valid`, drop column `certificateID`, drop column `statusChangedBy`;

drop table pqfcatdata;
alter table pqfcategories drop foreign key  `FK_pqfcategories` ;
alter table pqfsubcategories drop foreign key  `FK_pqfsubcategories` ;
drop table pqfcategories;
drop table pqfsubcategories;
drop table pqfopmatrix;
drop table pqfquestions;
drop table audit_operator;
drop table desktopmatrix;

ALTER TABLE `audit_type` 
	DROP COLUMN `mustVerify`, 
	DROP COLUMN `hasRequirements`;

ALTER TABLE `contractor_audit` 
	DROP COLUMN `auditStatus`, 
	DROP COLUMN `completedDate`, 
	DROP COLUMN `closedDate`, 
	DROP COLUMN `percentComplete`, 
	DROP COLUMN `percentVerified`, 
	DROP KEY `auditTypeStatus`;

ALTER TABLE `contractor_audit_operator` 
	DROP COLUMN `valid`, 
	DROP COLUMN `certificateID`, 
	DROP COLUMN `notes`, 
	DROP COLUMN `reason`, COMMENT='';

-- End of DDL changes (should be last)
