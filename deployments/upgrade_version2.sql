-- mysqldump --opt pics app_index app_index_stats audit_type audit_category audit_question audit_category_rule audit_type_rule workflow workflow_step pqfoptions | mysql -A --host=db2.picsauditing.com -u tallred -p pics

-- move app_index AND app_index_stats
-- move audit_type, audit_category, audit_question
-- move audit_category_rule, audit_type_rule
-- move workflow, workflow_step
-- move pqfoptions
-- move app_translation (not if empty)

ALTER TABLE `employee_site` ENGINE=InnoDB;

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

insert into facilities (corporateID, opID) select 4, id from accounts where type = 'Operator';
insert into facilities (corporateID, opID) select 5, id from accounts where country = 'US' and type = 'Operator';
insert into facilities (corporateID, opID) select 6, id from accounts where country = 'CA' and type = 'Operator';
insert into facilities (corporateID, opID) select 7, id from accounts where country = 'AE' and type = 'Operator';


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
TRUNCATE TABLE temp_cao;
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

update contractor_audit_operator cao, contractor_audit ca set cao.statusChangedDate = ca.expiresDate where cao.auditID = ca.id and cao.statusChangedDate is null and cao.status IN ('Expired');
update contractor_audit_operator cao, contractor_audit ca set cao.statusChangedDate = ca.closedDate where cao.auditID = ca.id and cao.statusChangedDate is null and cao.status IN ('Complete','Approved');
update contractor_audit_operator cao, contractor_audit ca set cao.statusChangedDate = ca.completedDate where cao.auditID = ca.id and cao.statusChangedDate is null and cao.status IN ('Complete','Approved','Submitted','Resubmitted','Incomplete');
update contractor_audit_operator set statusChangedDate = updateDate where statusChangedDate IS NULL;


-- Expiring the CAOs based on audits
update contractor_Audit_operator cao
join contractor_audit ca on cao.auditID= ca.id
set cao.status = 'Expired'
where ca.expiresDate < Now()
and cao.status != 'Expired';


/*
 * Adding the workflow notes
 */ 
-- insert CAOW notes for contractor reason for policies
insert into contractor_audit_operator_workflow 
select null,12,12,creationDate,updateDate,id,'Submitted','Pending',reason
from contractor_audit_operator
where reason > '' and reason is not null
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


/**
 * adding operator tagsfor adHoc audits  
 */
drop table if exists temp_auditoperatortags;
create table temp_auditoperatortags as  
select ao.opid,at.id, at.auditname
from audit_operator ao 
join audit_type at on at.id = ao.auditTypeID
join accounts a on a.id = ao.opid
join audit_type_rule atr on atr.auditTypeID = ao.auditTypeID 
and atr.opid = ao.opid
where ao.cansee =1 and ao.minRiskLevel = 0 and ao.tagID is null and at.id not between 113 and 115
group by atr.opid, atr.audittypeid 
order by ao.opid , ao.auditTypeID;

insert into operator_tag 
select null,substr(aot.auditname,1,49),1,aot.opID,1098,1098, 
Now(),Now(),0,1
from temp_auditoperatortags aot;

update audit_type_rule atr
join temp_auditoperatortags aot on atr.auditTypeID = aot.id and atr.opid = aot.opid
join operator_tag ot on ot.opid = aot.opid
set atr.tagID = ot.id
where ot.tag = substr(aot.auditname,1,49);

CREATE TEMPORARY TABLE audittypetags as
select atr.opid as operatorid, atr.audittypeid as audittypeid, atr.tagID from audit_type_rule atr
join temp_auditoperatortags aot on atr.auditTypeID = aot.id and atr.opid = aot.opid
join operator_tag ot on ot.opid = aot.opid
where ot.tag = substr(aot.auditname,1,49)
group by atr.opid, atr.audittypeid, atr.tagID;

insert into contractor_tag 
select null,ca.conid,att.tagid,1098,1098,Now(),Now() from audittypetags att
join generalcontractors gc on gc.genid = att.operatorid
join contractor_audit ca on ca.audittypeid = att.audittypeid and ca.conid = gc.subid
group by ca.conid, att.tagid;


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
drop table pqfquestion_text;

ALTER TABLE `contractor_audit` 
	DROP COLUMN `auditStatus`, 
	DROP COLUMN `completedDate`, 
	DROP COLUMN `closedDate`, 
	DROP COLUMN `percentComplete`, 
	DROP COLUMN `percentVerified`;

alter table `operators` drop column `inheritAudits`, drop column `inheritAuditCategories`, drop column `inheritInsurance`;

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
delete from flag_criteria where id = 170;

update `email_template` set `id`='10',`accountID`='1100',`templateName`='Insurance certificates about to expire',`subject`='Insurance certificates about to expire',`body`='Attn: <ContactName>,\r\n\r\nThis is an automatic reminder that the following Policies for <CompanyName> have or are about to expire.\r\n\r\n#foreach($outer in $contractor.audits)\r\n#if($outer.auditType.classType.toString() == ''Policy'')\r\n#foreach($inner in $contractor.audits)\r\n#if($inner.id != $outer.id && $inner.auditType == $outer.auditType && $inner.getExpiringPolicies())\r\n#foreach($operator in $outer.operators)\r\n#if($operator.status.toString() == ''Pending'')\r\n$inner.auditType.auditName for $operator.operator.name Expires On $pics_dateTool.format(''yyyy-MM-dd'',$inner.expiresDate)\r\n#end\r\n#end\r\n#end\r\n#end\r\n#end\r\n#end\r\n\r\nPlease upload a new insurance certificate using the insurance requirements of the above.\r\n\r\nIf we do not receive this certificate prior to the expiration you may not be permitted to enter the facility.\r\n\r\nAs always we appreciate your cooperation and are here to answer any questions you may have. Please reply to <CSRName> at <CSREmail> with any questions.\r\n\r\nThank you,\r\n<CSRName>\r\nPICS\r\nP.O. Box 51387\r\nIrvine CA 92619-1387\r\ntel: <CSRPhone>\r\nfax: <CSRFax>\r\n<CSREmail>\r\nhttp://www.picsauditing.com',`createdBy`='941',`creationDate`='2008-09-29 00:00:00',`updatedBy`='938',`updateDate`='2009-12-23 10:36:17',`listType`='Contractor',`allowsVelocity`='1',`html`='0',`recipient`=NULL where `id`='10';
update `email_template` set `id`='65',`accountID`='1100',`templateName`='Flag Color',`subject`='${flagColor} Flagged Contractors',`body`='<SubscriptionHeader>\r\n\r\nThere are <b>${flags.size()} contractors</b> whose#if(${user.account.corporate}) Overall Corporate#end flag color is currently <b>${flagColor}<b>\r\n<img src=\"http://www.picsauditing.com/images/icon_${flagColor.toString().toLowerCase()}Flag.gif\" width=\"10\" height=\"12\">.\r\n\r\n<br /><br />\r\n\r\n<table style=\"border-collapse: collapse; border: 2px solid #003768; background: #f9f9f9;\">\r\n <thead>\r\n  <tr style=\"vertical-align: middle; font-size: 13px;font-weight: bold; background: #003768; color: #FFF;\">\r\n#if(${user.account.corporate} && !${flagColor.toString().equals(\"Green\")})\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Operator Name</td>\r\n#end\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Contractor Name</td>\r\n#if(${user.account.operator} || !${flagColor.toString().equals(\"Green\")})\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Changed On</td>\r\n   <td style=\"border: 1px solid #e0e0e0; padding: 4px;\">Waiting On\r\n       <a href=\"http://help.picsauditing.com/wiki/Waiting_On\" style=\"font-weight: bold; color: white;\" title=\"Definition\">?</a>\r\n   </td>\r\n#end\r\n  </tr>\r\n </thead>\r\n <tbody>\r\n  #foreach( $co in $flags )\r\n  <tr style=\"margin:0px\">\r\n#if(${user.account.corporate} && !${flagColor.toString().equals(\"Green\")})\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\"><a href=\"http://www.picsorganizer.com/ContractorFlag.action?id=$co.contractorAccount.id&opID=$co.operatorAccount.id\">$co.operatorAccount.name</a></td>\r\n#end\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\"><a href=\"http://www.picsorganizer.com/ContractorView.action?id=$co.contractorAccount.id\">$co.contractorAccount.name</a></td>\r\n#if(${user.account.operator} || !${flagColor.toString().equals(\"Green\")})\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">#if($co.flagLastUpdated)$pics_dateTool.format(\'MM/dd/yy\', $co.flagLastUpdated)#else -#end </td>\r\n   <td style=\"border: 1px solid #A84D10; vertical-align: middle; padding: 4px; font-size: 13px;\">#if($co.waitingOn.toString() == \'None\')None#end#if($co.waitingOn.toString() == \'Contractor\')Contractor#end#if($co.waitingOn.toString() == \'PICS\')PICS#end#if($co.waitingOn.toString() == \'Operator\')Operator#end</td>\r\n#end\r\n  </tr>\r\n  #end\r\n </tbody>\r\n</table>\r\n\r\n<p>\r\n<a style=\"color: #003768; padding-left: 17px; margin-left: 2px; background: url(\'http://www.picsorganizer.com/images/help.gif\') no-repeat left center;\"\r\n href=\"http://help.picsauditing.com/wiki/Reviewing_Flag_Status\">Contractor Flag Colors Explained</a><br>\r\n<a style=\"color: #003768; padding-left: 17px; margin-left: 2px; background: url(\'http://www.picsorganizer.com/images/help.gif\') no-repeat left center;\"\r\n href=\"http://help.picsauditing.com/wiki/Waiting_On\">Waiting On Status Explained</a>\r\n</p>\r\n\r\n<TimeStampDisclaimer>\r\n\r\n<SubscriptionFooter>',`createdBy`='2357',`creationDate`='2009-08-03 18:19:38',`updatedBy`='20952',`updateDate`='2010-04-30 11:24:49',`listType`='Contractor',`allowsVelocity`='1',`html`='1',`recipient`='Admin' where `id`='65';
delete from token where tokenID = 6;

/**
 * Data conversion for WCB 
 */
create TEMPORARY table temp_wcb1
select at.id audittypeid, ac.id categoryid, rs.isocode from audit_category ac
join audit_type at on at.id = ac.audittypeid
join ref_state rs on rs.countryCode = 'CA' and auditname like concat(rs.english,'%')
where(auditname like 'Alberta%'
or auditname like 'Ontario%'
or auditname like 'British Columbia%'
or auditname like 'Nova Scotia%'
or auditname like 'Manitoba%'
or auditname like 'New Brunswick%'
or auditname like 'Quebec%'
or auditname like 'Saskatchewan%'
or auditname like 'Prince Edward Island%'
or auditname like 'Newfoundland and Labrador%');

insert into contractor_audit 
select null,wcb.audittypeid, 
conID,ca.creationDate,ca.createdBy,ca.updateDate,ca.updatedBy,expiresDate, 
ca.auditorID,assignedDate,scheduledDate,requestedByOpID,auditLocation,contractorConfirm, 
auditorConfirm,manuallyAdded,auditFor,needsCamera,lastRecalculation,score, 
closingAuditorID,contractorContact,phone,phone2,address,address2,city,state, 
zip,country,latitude,longitude,paidDate
from contractor_audit ca
join pqfdata pd on pd.auditid =  ca.id
join audit_question aq on aq.id = pd.questionid
join temp_wcb1 wcb on wcb.isocode = pd.answer
where aq.name like '%select province%'
and pd.answer in ('AB','ON','BC','NS','MB','NB','QC','SK','PE','NL')
and ca.auditTypeID = 11;

insert into audit_cat_data 
select null,canew.id,wcb.categoryid, 
0,0,8,1,0,0,0,0,0.000,0,1098,1098,Now(),Now()
from contractor_audit canew
join temp_wcb1 wcb on canew.audittypeid = wcb.audittypeid;

select * from audit_type where id = 59;
delete from contractor_audit where audittypeid=59;

create table temp_question as
select aq.id as newqid, at.id as oldqid from audit_question aq
join temp_wcb1 wcb on wcb.categoryid = aq.categoryid
join audit_question at on at.categoryid = 210 
and 
(aq.name like concat('%',at.name,'%')
or 
(aq.name like '%Please upload your WCB%' and at.name like 'Please upload your WCB%')
or 
(aq.name like '%Net Premium Rate:%' and at.name like 'Net Premium Rate%')
);

select conid, auditTypeID,count(*) from contractor_Audit
group by conid, auditTypeID
having count(*) > 2 order by auditTypeID  desc;

select * 
from contractor_Audit canew 
join temp_wcb1 wcb on wcb.audittypeid = canew.auditTypeID
join (select pd.*  from pqfdata pd  
join contractor_audit ca on pd.auditID = ca.id
join audit_question aq on aq.id = pd.questionid 
where aq.categoryid = 210
and aq.id = 2998) t on t.answer = wcb.isocode
JOIN contractor_Audit ca on ca.id = t.auditid and ca.conid = canew.conid
join pqfdata pd on pd.auditid = ca.ID
join temp_question tq on tq.oldqid = pd.questionid
join audit_question aq on aq.categoryid = wcb.categoryid and aq.id = tq.newqid
where pd.questionid in 
(select id from audit_question where categoryid = 210 and number between 2 and 8 order by number);



update contractor_Audit canew 
join temp_wcb1 wcb on wcb.audittypeid = canew.auditTypeID
join (select pd.*  from pqfdata pd  
join contractor_audit ca on pd.auditID = ca.id
join audit_question aq on aq.id = pd.questionid 
where aq.categoryid = 210
and aq.id = 2998) t on t.answer = wcb.isocode
JOIN contractor_Audit ca on ca.id = t.auditid and ca.conid = canew.conid
join pqfdata pd on pd.auditid = ca.ID
join temp_question tq on tq.oldqid = pd.questionid
join audit_question aq on aq.categoryid = wcb.categoryid and aq.id = tq.newqid
set pd.auditid = canew.id, pd.questionid = aq.id
where pd.questionid in 
(select id from audit_question where categoryid = 210 and number between 2 and 8 order by number)
and ca.conid not in (10467,11784,11854,12735);

/**
 * Removing the operator requirements category
 */
delete from pqfdata where questionid in (
select id from audit_question where categoryid in (559,562,565,568));
delete from audit_question where categoryid in (559,562,565,568);

delete from audit_category 
where auditTypeID in (select id from audit_type where classType = 'Policy')
and name = 'Operator Requirements';

