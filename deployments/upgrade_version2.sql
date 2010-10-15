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


------ ????? what about corporate 4 - PICS GLOBAL
insert into facilities (corporateID, opID) select 4, id from accounts where type = 'Operator';
-- be sure that 5 and 6 are in the accounts table first
insert into facilities (corporateID, opID) select 5, id from accounts where country = 'US' and type = 'Operator';
insert into facilities (corporateID, opID) select 6, id from accounts where country = 'CA' and type = 'Operator';


-- convert the cert and is valid on the Policy CAO to the new policy questions for each operator
insert into pqfdata 
(auditID, questionID, answer, dateVerified, auditorID, createdBy, creationDate, updatedBy, updateDate)
select cao.auditID, q.id, cao.valid, cao.statusChangedDate, cao.statusChangedBy, cao.createdBy, cao.creationDate, cao.updatedBy, cao.updateDate
from contractor_audit_operator cao
join contractor_audit ca on ca.id = cao.auditID
join audit_category ac on ac.auditTypeID = ca.auditTypeID
join temp_cao_conversion t on t.auditTypeID = ca.auditTypeID and t.opID = cao.opID and ac.legacyID = t.id
join audit_question q on q.categoryID = ac.id and q.number = 2
where valid > '';

insert into pqfdata 
(auditID, questionID, answer, dateVerified, auditorID, createdBy, creationDate, updatedBy, updateDate)
select cao.auditID, q.id, certificateID, cao.statusChangedDate, cao.statusChangedBy, cao.createdBy, cao.creationDate, cao.updatedBy, cao.updateDate
from contractor_audit_operator cao
join contractor_audit ca on ca.id = cao.auditID
join audit_category ac on ac.auditTypeID = ca.auditTypeID
join temp_cao_conversion t on t.auditTypeID = ca.auditTypeID and t.opID = cao.opID and ac.legacyID = t.id
join audit_question q on q.categoryID = ac.id and q.number = 1
where cao.certificateID > 0;

-- Conversion for Policy CAOs
update contractor_audit_operator 
set status = 'Incomplete'
where status = 'Rejected';

update contractor_audit_operator 
set status = 'Complete'
where status = 'Verified';

/*
 * BEGIN: CAO Conversion for Non-Policies (audits, pqf, etc)
 */
-- TRUNCATE TABLE temp_cao;
INSERT INTO temp_cao 
SELECT null, ca.conID, t.opID, t.gbID, ca.auditTypeID, ca.id auditID, ca.auditStatus
FROM contractor_info c
JOIN contractor_audit ca ON ca.conid = c.id
LEFT JOIN (
SELECT gc.subid conID, o.id opID, o.inheritAudits gbID, ao.auditTypeID FROM generalcontractors gc
JOIN operators o ON o.id = gc.genid
JOIN accounts a ON a.id = gc.genid and a.type = 'Operator'
join audit_operator ao on ao.opID = o.inheritAudits and canSee = 1
) t ON ca.conID = t.conID and ca.auditTypeID = t.auditTypeID
JOIN audit_type atype ON atype.id = ca.auditTypeID and classtype != 'Policy'
;

-- index temp_cao ??

-- Clean up the governing bodies for annual updates and non-pqf audits
update temp_cao t, accounts a
set t.gbid = (case when a.country = 'CA' then 6 else 5 end)
where t.opid = a.id
and t.audittypeid = 11;

update temp_cao t, accounts a
set t.gbid = 4 -- PICS Global
where t.opid = a.id
and t.audittypeid not in (1,11);

insert into contractor_audit_operator (auditID, opID, status, visible, percentComplete, percentVerified, createdBy, updatedBy, creationDate, updateDate)
select distinct
 ca.id            auditID,
 t.gbID           opID,
 ca.auditStatus   status,
 1                visible,
 percentComplete,
 percentVerified,
 createdBy, updatedBy, creationDate, updateDate
from temp_cao t
join contractor_audit ca on t.auditID = ca.id;

insert into contractor_audit_operator_permission (caoID, opID)
select distinct cao.id, t.opID from contractor_audit_operator cao
join temp_cao t on cao.auditID = t.auditID and t.gbID = cao.opID;

update contractor_audit_operator set status = 'Complete' where status IN ('Active');

select count(*) from contractor_audit_operator where statusChangedDate is null;

update contractor_audit_operator cao, contractor_audit ca set cao.statusChangedDate = ca.expiresDate where cao.auditID = ca.id and cao.statusChangedDate is null and cao.status IN ('Expired');
update contractor_audit_operator cao, contractor_audit ca set cao.statusChangedDate = ca.closedDate where cao.auditID = ca.id and cao.statusChangedDate is null and cao.status IN ('Complete','Approved');
update contractor_audit_operator cao, contractor_audit ca set cao.statusChangedDate = ca.completedDate where cao.auditID = ca.id and cao.statusChangedDate is null and cao.status IN ('Complete','Approved','Submitted','Resubmitted','Incomplete');
update contractor_audit_operator set statusChangedDate = updateDate where statusChangedDate IS NULL;

/*
 * Adding the workflow notes
 */ 
-- insert CAOW notes for contractor reason for policies
insert into contractor_audit_operator_workflow 
select null,12,12,creationDate,updateDate,id,'Submitted','Pending',reason
from contractor_audit_operator
where reason > '' and eason is not null
and reason not in ('N/A','None','na');

-- insert CAOW notes for operator notes for policies
insert into contractor_audit_operator_workflow 
select null,statusChangedBy,statusChangedBy,statusChangedDate,
statusChangedDate,id,status,'Submitted',notes
from contractor_audit_operator
where notes > '' and notes is not null;

-- insert CAOW notes for audits
insert into contractor_audit_operator_workflow 
select null,ca.createdBy,ca.updatedBy,ca.completedDate, 
ca.completedDate,cao.id,'Submitted','Pending',null
from contractor_audit ca
join contractor_audit_operator cao on cao.auditid = ca.id
join audit_type yat on yat.id = ca.audittypeid
where yat.classType != 'Policy'
and ca.completedDate > 0;

insert into contractor_audit_operator_workflow 
select null,ca.createdBy,ca.updatedBy,ca.closedDate, 
ca.closedDate,cao.id,'Complete','Submitted',null
from contractor_audit ca
join contractor_audit_operator cao on cao.auditid = ca.id
join audit_type yat on yat.id = ca.audittypeid
where yat.classType != 'Policy'
and ca.closedDate > 0;

-- insert the Audit Category Data for subcategories
-- huh?? We may not need this. Keerthi and Trevor can't quite agree if it's needed
insert into audit_cat_data
select null,acd.auditID,acs.id,acd.requiredCompleted,acd.numRequired,acd.numAnswered, 
acd.applies,acd.percentCompleted,acd.percentVerified,acd.percentClosed,acd.override, 
acd.score,acd.scoreCount,acd.createdBy,acd.updatedBy,acd.creationDate,acd.updateDate
from audit_category acp
join audit_category acs on acp.id = acs.parentID
join audit_cat_data acd on acd.categoryID = acp.id
join audit_type at on at.id = acp.audittypeid
left join audit_Cat_data acds on acds.auditid = acd.auditid and acds.categoryid = acs.id
where acp.parentID is null
and at.classType != 'Policy'
and acds.id is null;

-- remove data for What percentage of your services are performed for the Candadian Government?
delete from pqfdata where questionID = 2035;

-- For Policies 
update flag_criteria fc
set fc.requiredStatus = 'Submitted'
where validationRequired = 0;

update flag_criteria fc
set fc.requiredStatus = 'Complete'
where validationRequired = 1;

update flag_criteria fc
join audit_type at on at.id = fc.auditTypeID
set fc.requiredStatus = 'Approved'
where at.classType = 'Policy'
and validationRequired = 1;

-- For Non Policies 
update flag_criteria fc
join audit_type at on at.id = fc.auditTypeID
set fc.requiredStatus = 'Complete'
where at.classType != 'Policy'
and validationRequired = 1;

/*  DDL Changes
 *  Dropping Tables and Columns
 */
alter table contractor_audit_operator drop column `notes`, drop column `reason`, drop column `valid`, drop column `certificateID`, drop column `statusChangedBy`;

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

	alter table flag_criteria drop column `validationRequired`;
-- End of DDL changes (should be last)
-- End of DDL changes (should be last)
-- Adding permissions for the configuration pages
insert into useraccess 
(userID,accessType,viewFlag,editFlag,deleteFlag,grantFlag,lastUpdate,grantedByID)
values
(941,'ManageCategoryRules',1,1,1,1,Now(),1098),
(941,'ManageAuditTypeRules',1,1,1,1,Now(),1098),
(941,'ManageAuditWorkFlow',1,1,1,1,Now(),1098);
	
select * from flag_criteria where auditTypeID not in (select id from audit_type)
and auditTypeID is not null;
	
-- TODO Create completed welcome call audits for all contractors that don't already have one

	-- test
