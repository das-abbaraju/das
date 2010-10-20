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


/**
 * adding operator tagsfor adHoc audits  
 */
create table auditoperatortags as  
select ao.opid,at.id, at.auditname
from pics_yesterday.audit_operator ao 
join audit_type at on at.id = ao.auditTypeID
join accounts a on a.id = ao.opid
join pics_alpha2.audit_type_rule atr on atr.auditTypeID = ao.auditTypeID 
and atr.opid = ao.opid
where ao.cansee =1 and ao.minRiskLevel = 0
group by atr.opid, atr.audittypeid 
order by ao.opid , ao.auditTypeID;

insert into operator_tag 
select null,substr(aot.auditname,1,49),1,aot.opID,1098,1098, 
Now(),Now(),0,1
from auditoperatortags aot;

select * from audit_type_rule atr
join pics_yesterday.audit_operator ao on ao.opid = atr.opid and ao.audittypeid = atr.auditTypeID
where ao.cansee = 1 and ao.minrisklevel = 0 and atr.include = 1;

update audit_type_rule atr
join auditoperatortags aot on atr.auditTypeID = aot.id and atr.opid = aot.opid
join operator_tag ot on ot.opid = aot.opid
set atr.tagID = ot.id
where ot.tag = substr(aot.auditname,1,49);

CREATE TEMPORARY TABLE audittypetags as
select atr.opid as operatorid, atr.audittypeid as audittypeid, atr.tagID from audit_type_rule atr
join auditoperatortags aot on atr.auditTypeID = aot.id and atr.opid = aot.opid
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

/**
 * Updating audit questions -- remove links from question names, put into helptext
 * Here's the query
 * 
select distinct q.id, trim(left(q.name, instr(q.name,'<') - 1)) as qtext, substr(q.name from instr(q.name,'<') + 4) as link
from audit_category c
join audit_question q on q.categoryID = c.id
where c.auditTypeID = 2
and instr(q.name,'<') > 0;
 */
UPDATE audit_question SET name='Does the program provide for a training program for all employees who are exposed to airborne concentrations? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9995" target="_BLANK">Fed OSHA 1910.1001(j)(7)(i)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9995" target="_BLANK">Fed OSHA 1915.1001(b)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=10862&p_table=STANDARDS" target="_BLANK">Fed OSHA 1910.1101</a>' WHERE id=899;
UPDATE audit_question SET name='Does the program have established regulated airborne concentrations of asbestos? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9995#1910.1001?" target="_BLANK">Fed OSHA 1910.1001(e)(1)</a>' WHERE id=901;
UPDATE audit_question SET name='Does the program provide for engineering controls & work practices to reduce/maintain the exposure below TWA? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9995#1910.1001?" target="_BLANK">Fed OSHA 1910.1001(f)(1)(i)</a>' WHERE id=902;
UPDATE audit_question SET name='Does your program include the duties of a compentent person?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=10862&p_table=STANDARDS#1926.1101(o)" target="_BLANK">Fed OSHA 1926.1101(o)</a>' WHERE id=4306;
UPDATE audit_question SET name='Does the program address specific control methods for Class I-IV Asbestos work?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=10862&p_table=STANDARDS#1926.1101(o)(4)" target="_BLANK">Fed OSHA 1926.1101(o)(4)</a>' WHERE id=4307;
UPDATE audit_question SET name='Does the program provide for training? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9797" target="_BLANK">Fed OSHA 1910.146(g)(1)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 5</a>' WHERE id=944;
UPDATE audit_question SET name='Does the program address provisions and procedures for protection of employees during entry into permit required confined spaces? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9800" target="_BLANK">Fed OSHA 1910.146(a)(1)(d)(1-5)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 5</a>' WHERE id=947;
UPDATE audit_question SET name='Does the program address provisions for providing an attendant? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9801" target="_BLANK">Fed OSHA 1910.146(d)(6)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 5</a>' WHERE id=948;
UPDATE audit_question SET name='Does the program describe duties of entrants, attendants, and entry supervisors? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9797" target="_BLANK">Fed OSHA 1910.146(h)(i)(j)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 5</a>' WHERE id=951;
UPDATE audit_question SET name='Does the program address procedures for summoning rescue and emergency services, for rescuing entrants, providing first aid, and for preventing unauthorized personnel from attempting a rescue? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9797" target="_BLANK">Fed OSHA 1910.146(d)(9) & (k)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 5</a>' WHERE id=952;
UPDATE audit_question SET name='Does the program include procedures to coordinate operations if multi employers are working in the same confined space? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9797" target="_BLANK">Fed OSHA 1910.146(d)(11)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 5</a>' WHERE id=954;
UPDATE audit_question SET name='Does the program address training and qualifications of dive team members? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9979" target="_BLANK">1910.410(a)(1)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 31</a>' WHERE id=970;
UPDATE audit_question SET name='Does the program address that ALL Dive Team members have current First Aid and CPR Training? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9979#1910.410(a)(3)" target="_BLANK">1910.410(a)(3)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 31</a>' WHERE id=971;
UPDATE audit_question SET name='Does the employer provide each employee with a copy of the Safe Practice Manual? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9980" target="_BLANK">1910.420(a)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 31</a>' WHERE id=972;
UPDATE audit_question SET name='Does the program specify what safe practices shall be included for each diving mode? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9980#1910.420(b)(2)" target="_BLANK">1910.420(b)(2)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 31</a>' WHERE id=974;
UPDATE audit_question SET name='Does the program address training requirements for employees that face a higher than normal risk of eletrical accident?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9909#1910.332(b)(1)" target="_BLANK">Fed OSHA 1910.332(b)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9909#1910.332(c)" target="_BLANK">Fed OSHA 1910.332(c) Table S-4</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=975;
UPDATE audit_question SET name='Does the program address safety-related work practices to prevent electric shock? i.e. Ground Fault Circuit Interrupter (GFCI)', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(a)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10705#1926.404(b)(1)(i)" target="_BLANK">Fed OSHA 1926.404(b)(1)(i)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=976;
UPDATE audit_question SET name='Does the program address working on or near exposed deenergized and energized parts? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(b)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=977;
UPDATE audit_question SET name='Does the program address working on or near exposed energized parts? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(c)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_doc" target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=979;
UPDATE audit_question SET name='Does the program address what procedures are used for working under overhead lines? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(c)(3)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=981;
UPDATE audit_question SET name='Does the program discuss illumination? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910#1910.333(c)(4)(i)" target="_BLANK">Fed OSHA 1910.333(c)(4)(i)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=984;
UPDATE audit_question SET name='Does the program address what protective measures are to be used when working in confined or enclosed work spaces where electrical hazards may exist? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(c)(5)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=985;
UPDATE audit_question SET name='Does the program address conductive materials and equipment? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(c)(6)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=986;
UPDATE audit_question SET name='Does the program specify training requirements for ''qualified'' persons? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9909" target="_BLANK">Fed OSHA 1910.332(a)(b)(3)(i)(ii)(iii)(c)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=989;
UPDATE audit_question SET name='Does the program address safety-related work practices to prevent electric shock? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(a)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=990;
UPDATE audit_question SET name='Does the program address working on or near exposed deenergized parts? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(b)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=991;
UPDATE audit_question SET name='Does the program address working on or near exposed energized parts? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(c)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=993;
UPDATE audit_question SET name='Does the program address what procedures are used for working under overhead lines? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(c)(3)</a>' WHERE id=995;
UPDATE audit_question SET name='Does the program discuss illumination? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(c)(4)(i)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=998;
UPDATE audit_question SET name='Does the program address what protective measures are to be used when working in confined or enclosed work spaces? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(c)(5)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=999;
UPDATE audit_question SET name='Does the program address conductive materials and equipment? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9910" target="_BLANK">Fed OSHA 1910.333(c)(6)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10704 " target="_BLANK">Fed OSHA 1926.403</a>' WHERE id=1000;
UPDATE audit_question SET name='Does the program address mandated training requirements? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10759" target="_BLANK">Fed OSHA 1926.503(a)(1), 1926.503(b)(1-2), 1926.503(c)(1-3)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 9</a>' WHERE id=1003;
UPDATE audit_question SET name='Does the program address when fall protection must be provided for employees? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10757" target="_BLANK">Fed OSHA 1926.501</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 9</a>' WHERE id=1004;
UPDATE audit_question SET name='Does the program address a safety monitoring system and controlled access zones? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10758" target="_BLANK">Fed OSHA 1926.502(k(8)</a>' WHERE id=1007;
UPDATE audit_question SET name='Does the program address guidelines of Fall Protection equipment use? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10758" target="_BLANK">Fed OSHA 1926.502</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 9</a>' WHERE id=1010;
UPDATE audit_question SET name='Does your program address a Fall Protection Plan, when conventional Fall Protection equipment cannot be used?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=10758&p_table=STANDARDS#1926.502(k)" target="_BLANK">Fed OSHA 1926.502(k)</a>' WHERE id=4308;
UPDATE audit_question SET name='Does the program specify training requirements for the use of portable fire extinguishers? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9811" target="_BLANK">Fed OSHA 1910.157(g)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10671" target="_BLANK">Fed OSHA 1926.150(c)(xi)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9752" target="_BLANK">Fed OSHA 1910.106</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 10</a>' WHERE id=1011;
UPDATE audit_question SET name='Does the contractor have a program on Combustible & Flammable Liquids Handling?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10671 " target="_BLANK">Fed OSHA 1926.150 - 152</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=12887" target="_BLANK">Fed OSHA 1910.39</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 10</a>' WHERE id=1316;
UPDATE audit_question SET name='Does the contractor have a Bloodborne Pathogens program that addresses hazards to employees? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10051" target="_BLANK">Fed OSHA 1910.1030</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9806" target="_BLANK">Fed OSHA 1910.151</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 11</a>' WHERE id=917;
UPDATE audit_question SET name='Does the program state that first aid and medical facilities will be made available and in the absence of facilities that someone trained to render First Aid will be provided on the job site? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10622#1926.50(c)" target="_BLANK">Fed OSHA 1926.50(c)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9806 " target="_BLANK">Fed OSHA 1910.151</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=INTERPRETATIONS&p_id=25627" target="_BLANK">Letter of Interpretation</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 11</a>' WHERE id=1014;
UPDATE audit_question SET name='Does the program address availability of first aid supplies? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10622#1926.50(d)(1)" target="_BLANK">Fed OSHA 1926.50(d)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9806 " target="_BLANK">Fed OSHA 1910.151</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 11</a>' WHERE id=1016;
UPDATE audit_question SET name='Does the program specify the position responsible for accessibility, checking contents, & how often contents are to be checked? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10622" target="_BLANK">Fed OSHA 1926.50(d)(2)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9806 " target="_BLANK">Fed OSHA 1910.151</a>' WHERE id=1018;
UPDATE audit_question SET name='Does the program specify procedures in getting injured persons to a physician or hospital? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10622" target="_BLANK">Fed OSHA 1926.50(e)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9806" target="_BLANK">Fed OSHA 1910.151</a>' WHERE id=1019;
UPDATE audit_question SET name='Does the program address preparing for emergency transportation to the nearest health care facility in the event of an injury or illness?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 11</a>' WHERE id=3114;
UPDATE audit_question SET name='Does the program address requirements for first aid providers? If all employees are certified in First Aid, please state this in your program.', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 11</a>' WHERE id=3115;
UPDATE audit_question SET name='Does the program address that all injuries and illnesses are reported to the employer and documented?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 11</a>' WHERE id=3116;
UPDATE audit_question SET name='Does the program specify that only trained and certified operators, including supervisors, are allowed to operate the device?  (including refresher training)', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9828#1910.178(l)" target="_BLANK">Fed OSHA 1910.178(l) Training</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 19, Sec 283</a>' WHERE id=1022;
UPDATE audit_question SET name='Do the written training programs include both formal instruction, practical training, and operator evaluation in the workplace?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9828#1910.178(l)(2)(ii)" target="_BLANK">Fed OSHA 1910.178(l)(2)(ii)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 19, Sec 283</a>' WHERE id=1023;
UPDATE audit_question SET name='Training Program Content:  Does the training program content list all minimally-required items (23 items), including load capacity, instructions, distance, differences between cars vs. PITs, refueling/recharging, ramps, visibility, balance/counterbalance, etc.? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9828" target="_BLANK">Fed OSHA 1910.178 (l)(3)</a>' WHERE id=1025;
UPDATE audit_question SET name='Does the program address mandatory refresher training and recertification? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9828" target="_BLANK">Fed OSHA 1910.178 (l) (4)(ii)</a>' WHERE id=1026;
UPDATE audit_question SET name='Does the program address daily operator inspections?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9828#1910.178(q)(7)" target="_BLANK">Fed OSHA 1910.178(q)(7)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 19, Sec 283</a>' WHERE id=1028;
UPDATE audit_question SET name='Does the program specify procedures & guidelines to eliminate all injuries resulting from possible malfunctions, improper grounding, and/or defective electrical tools? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10705#1926.404(b)(1)(iii)" target="_BLANK">Fed OSHA 1926.404(b)(1)(iii)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=1031;
UPDATE audit_question SET name='Does the program address how often inspection of cords and equipment are to be made and disposition of items that are found to be defective? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10705#1926.404(b)(1)(iii)(C)" target="_BLANK">Fed OSHA 1926.404(b)(1)(iii)(C)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=1034;
UPDATE audit_question SET name='Does the program stipulate restrictions for use of equipment that does not meet requirements? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10705#1926.404(b)(1)(iii)(F)" target="_BLANK">Fed OSHA 1926.404(b)(1)(iii)(F)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=1036;
UPDATE audit_question SET name='Does the program state that employees be trained in the area they are working in? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765#1910.120(q)(6)" target="_BLANK">Fed OSHA 1910.120(q)(6)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10651 " target="_BLANK">Fed OSHA 1926.65</a>' WHERE id=1055;
UPDATE audit_question SET name='Does the program address the different levels of required training: First Responder Awareness Level, First Responder Operations Level, Hazardous Materials Technician, Hazardous Materials Specialist and On Scene Incident Commander, where applicable?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765#1910.120(q)(6)(i)" target="_BLANK">Fed OSHA 1910.120(q)(6)(i)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10651 " target="_BLANK">Fed OSHA 1926.65</a>' WHERE id=1056;
UPDATE audit_question SET name='Does the program address the Hazardous Materials Technician and Materials Specialist? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9768" target="_BLANK">Fed OSHA 1910.120(q)(6)(iii)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10651 " target="_BLANK">Fed OSHA 1926.65</a>' WHERE id=1058;
UPDATE audit_question SET name='Does the program address On-Scene Incident Commander? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765" target="_BLANK">Fed OSHA 1910.120(q(6)(v)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10651 " target="_BLANK">Fed OSHA 1926.65</a>' WHERE id=1060;
UPDATE audit_question SET name='Does the program address refresher training shall be conducted on or before the initial training date of expiration? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765#1910.120(q)(8)" target="_BLANK">Fed OSHA 1910.120(q)(8)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10651 " target="_BLANK">Fed OSHA 1926.65</a>' WHERE id=1062;
UPDATE audit_question SET name='Does the program specify requirements of chemical protective clothing and equipment to be used by hazardous material specialists? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765" target="_BLANK">Fed OSHA 1910.120(q)(10)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10651 " target="_BLANK">Fed OSHA 1926.65</a>' WHERE id=1067;
UPDATE audit_question SET name='Does your program address PPE hazard assessments?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=standards&p_id=9765#1910.120(g)" target="_BLANK">Fed OSHA 1910.120(g)</a>' WHERE id=4301;
UPDATE audit_question SET name='Does the program provide for appropriate training before employees are allowed to work? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765" target="_BLANK">Fed OSHA 1910.120(e)(1)(i)</a>' WHERE id=1069;
UPDATE audit_question SET name='Does the program address engineering controls, monitoring, work practices, and personal protective equipment for employee protection? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765" target="_BLANK">Fed OSHA 1910.120(g)(1)(i)</a>' WHERE id=1072;
UPDATE audit_question SET name='Does the program address provisions for monitoring to be performed where there may be employee exposure to hazardous substances? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765" target="_BLANK">Fed OSHA 1910.120(h)(1)(i)</a>' WHERE id=1073;
UPDATE audit_question SET name='Does the program provide for decontamination procedures? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765" target="_BLANK">Fed OSHA 1910.120(k)(2)(ii)</a>' WHERE id=1074;
UPDATE audit_question SET name='Does the program address the availability of regular showers & change rooms, if necessary? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9765" target="_BLANK">Fed OSHA 1910.120(k)(8)</a>' WHERE id=1087;
UPDATE audit_question SET name='Does the program address training requirements and refresher training? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10760 " target="_BLANK">Fed OSHA 1926.550</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 6</a>' WHERE id=961;
UPDATE audit_question SET name='Does the program address medical qualifications and follow-up evaluations? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10760 " target="_BLANK">Fed OSHA 1926.550</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 6</a>' WHERE id=962;
UPDATE audit_question SET name='Does the program state that a rating chart be provided in each crane cab? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10760 " target="_BLANK">Fed OSHA 1926.550</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 6</a>' WHERE id=1101;
UPDATE audit_question SET name='Does the program specify that inspection records and preventative maintenance records are maintained? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10760 " target="_BLANK">Fed OSHA 1926.550</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 6</a>' WHERE id=1102;
UPDATE audit_question SET name='Does the program specify availability of fire extinguishers? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10760 " target="_BLANK">Fed OSHA 1926.550</a>' WHERE id=1106;
UPDATE audit_question SET name='Does the program specify procedures for operations near electrical lines? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10760 " target="_BLANK">Fed OSHA 1926.550</a>' WHERE id=1107;
UPDATE audit_question SET name='Does the program address qualifications for riggers or who can attach or detach lifting equipment to loads or lifting loads?  ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10789" target="_BLANK">Fed OSHA 1926.753</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10760 " target="_BLANK">Fed OSHA 1926.550(g)(5)(iv)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 6</a>' WHERE id=1181;
UPDATE audit_question SET name='Does the program address that lifting devices are labelled?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 6 Sec 62 (1)(2)</a>' WHERE id=3121;
UPDATE audit_question SET name='Does the program address that lifting devices are only operated by competent workers?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 6 Sec 64 (1)</a>' WHERE id=3122;
UPDATE audit_question SET name='Does the program address the use of log books for lifting devices?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 6 Sec 65 (1)</a>' WHERE id=3123;
UPDATE audit_question SET name='Does the program address preventing unsafe lifts?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 6 Sec 66</a>' WHERE id=3124;
UPDATE audit_question SET name='Does the program address passing loads over work areas?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 6 Sec 62 (1)(2)</a>' WHERE id=3125;
UPDATE audit_question SET name='Does the program address restrictions on workers standing or passing under suspended loads?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 21</a>' WHERE id=3126;
UPDATE audit_question SET name='Does the program address energy control procedures?  Does it include electrical, steam, hydraulic, tension, gravity, etc. as potential sources of energy? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9804#1910.147(c)(1)" target="_BLANK">Fed OSHA 1910.147(c)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10718" target="_BLANK">Fed OSHA 1926.417</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=1115;
UPDATE audit_question SET name='Does the program discuss lockout/tagout devices and specify that lockout and tagout devices to be singularly identified to the individual placing the device?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9804#1910.147(c)(5)(ii)" target="_BLANK">Fed OSHA 1910.147(c)(5)(ii)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10718" target="_BLANK">Fed OSHA 1926.417</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=1116;
UPDATE audit_question SET name='Does the program discuss a lockout or tagout application? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9804" target="_BLANK">Fed OSHA 1910.147(d)(4)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10718" target="_BLANK">Fed OSHA 1926.417</a>' WHERE id=1123;
UPDATE audit_question SET name='Does the program have provisions for safety testing machines when the LOTO devices must be temporarily removed? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9804" target="_BLANK">Fed OSHA 1910.147(f)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10718" target="_BLANK">Fed OSHA 1926.417</a>' WHERE id=1126;
UPDATE audit_question SET name='Does the program have procedures for handling multiple groups of workers on the program? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9804#1910.147(f)(3)(i)" target="_BLANK">Fed OSHA 1910.147(f)(3)(i)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10718" target="_BLANK">Fed OSHA 1926.417</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 15</a>' WHERE id=1127;
UPDATE audit_question SET name='Does the program provide for employee training and access to information & training materials regarding hearing protection? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9735&p_table=STANDARDS" target="_BLANK">Fed OSHA 1910.95(k)(1)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 16</a>' WHERE id=1129;
UPDATE audit_question SET name='Does the program include monitoring procedures to be used when exposure limits exceed the established level? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9735&p_table=STANDARDS" target="_BLANK">Fed OSHA 1910.95 (d)(1)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 16</a>' WHERE id=1131;
UPDATE audit_question SET name='Does the program establish & maintain an annual audiometric testing program for all employees whose exposures equal or exceed the 8-hour time-weighted average of 85 decibels? Program must provide for establishment of a baseline audiogram for each exposed employee. ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9735&p_table=STANDARDS" target="_BLANK">Fed OSHA 1910.95(g)(2)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 16</a>' WHERE id=1132;
UPDATE audit_question SET name='Does the program provide for establishment of a baseline audiogram for each exposed employee? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9735&p_table=STANDARDS" target="_BLANK">Fed OSHA 1910.95(g)(5)(i)(ii)</a>' WHERE id=1133;
UPDATE audit_question SET name='Does the program provide for an annual audiogram? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9735&p_table=STANDARDS" target="_BLANK">Fed OSHA 1910.95(g)(6)       1910.95(g)(7)(i)     1910.95(g)(8)(i)</a>' WHERE id=1135;
UPDATE audit_question SET name='Does the program outline what steps are to be taken when a standard threshold shift occurs? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9735&p_table=STANDARDS" target="_BLANK">Fed OSHA 1910.95(g)(ii)(A)(B)(C)  (D)(g)(iii)(A)(B)</a>' WHERE id=1136;
UPDATE audit_question SET name='Does the program provide that hearing protectors are available to all employees exposed to an 8-hour time-weighted average of 85 decibels. ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9735&p_table=STANDARDS" target="_BLANK">Fed OSHA 1910.95(i)(1)(2)</a>' WHERE id=1137;
UPDATE audit_question SET name='Does the written program contain specific work-site information regarding where exposures may occur?   ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=FEDERAL_REGISTER&p_id=18341" target="_BLANK">SPECIAL PROGRAM</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 20</a>' WHERE id=1141;
UPDATE audit_question SET name='Does the program address procedures and methods for testing? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=FEDERAL_REGISTER&p_id=18341" target="_BLANK">SPECIAL PROGRAM</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 20</a>' WHERE id=1145;
UPDATE audit_question SET name='Are there at least 3 methods for employee protection addressed? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=FEDERAL_REGISTER&p_id=18341" target="_BLANK">SPECIAL PROGRAM</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 20</a>' WHERE id=1146;
UPDATE audit_question SET name='Does the program cover that each employee who may need to wear PPE be properly trained and/or retrained? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9777" target="_BLANK">Fed OSHA 1910.132(f)(1)(2)(3)(4)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10658" target="_BLANK">Fed OSHA 1926.95</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 18</a>' WHERE id=1147;
UPDATE audit_question SET name='Does the program state that PPE be provided, used, and maintained in a sanitary and reliable condition? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9777" target="_BLANK">Fed OSHA 1910.132(a)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10658" target="_BLANK">Fed OSHA 1926.95</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 18</a>' WHERE id=1148;
UPDATE audit_question SET name='Is there a policy to oversee employee-owned equipment? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9777#1910.132(b)" target="_BLANK">Fed OSHA 1910.132(b)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10658" target="_BLANK">Fed OSHA 1926.95</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 18</a>' WHERE id=1149;
UPDATE audit_question SET name='Does program state that selected PPE must be fitted to each affected employee? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9777" target="_BLANK">Fed OSHA 1910.132(d)(1)(iii)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10658" target="_BLANK">Fed OSHA 1926.95</a>' WHERE id=1152;
UPDATE audit_question SET name='Does the program address defective and damaged equipment? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9777" target="_BLANK">Fed OSHA 1910.132(e)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10658" target="_BLANK">Fed OSHA 1926.95</a>' WHERE id=1153;
UPDATE audit_question SET name='Does your program assess your workplace to determine if hazards are present, or likely to be present, which necessitate the use of PPE?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9777#1910.132(d)(1)" target="_BLANK">Fed OSHA 1910.132(d)(1)</a>' WHERE id=4304;
UPDATE audit_question SET name='Does the program discuss the purpose of Process Safety Management to include contract employer responsibilities? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9760" target="_BLANK">Fed OSHA 1910.119(h)(3)(i)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10646" target="_BLANK">Fed OSHA 1926.64</a>' WHERE id=1154;
UPDATE audit_question SET name='Does the program discuss hazards related to his/her job and provisions of an emergency action plan? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9760" target="_BLANK">Fed OSHA 1910.119(h)(3)(ii)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10646" target="_BLANK">Fed OSHA 1926.64</a>' WHERE id=1155;
UPDATE audit_question SET name='Does the program specify that training shall be documented? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9760" target="_BLANK">Fed OSHA 1910.119(h)(3)(iii)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10646" target="_BLANK">Fed OSHA 1926.64</a>' WHERE id=1156;
UPDATE audit_question SET name='Does the program discuss the purpose of Process Safety Management? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9760" target="_BLANK">Fed OSHA 1910.119</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10646" target="_BLANK">Fed OSHA 1926.64</a>' WHERE id=1157;
UPDATE audit_question SET name='Does the program discuss MOC (Management of Change)?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9760#1910.119(l)" target="_BLANK">Fed OSHA 1910.119(l)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10646" target="_BLANK">Fed OSHA 1926.64</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9760#1910.119(l)(3)" target="_BLANK">Fed OSHA 1910.119(l)(3)</a>' WHERE id=1419;
UPDATE audit_question SET name='Does the contractor have an Employee Evaluation Program?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9760#1910.119(h)(3)(iv)" target="_BLANK">Fed OSHA 1910.119(h)(3)(iv)</a>' WHERE id=4305;
UPDATE audit_question SET name='Does the program state that respiratory equipment will be provided for employee''s use against harmful vapors & oxygen deficient atmospheres at no charge to the employee? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=12716" target="_BLANK">Fed OSHA 1910.134(a)(1)(2)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 18</a>' WHERE id=1165;
UPDATE audit_question SET name='Does the program address fit-testing to include medical evaluation prior to fit-testing and required use of the respirator?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=12716" target="_BLANK">Fed OSHA 1910.134(e)(1)  1910.134(e)(4)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 18</a>' WHERE id=1170;
UPDATE audit_question SET name='Does the program address fit-testing?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=12716" target="_BLANK">Fed OSHA 1910.134(f)</a>' WHERE id=1171;
UPDATE audit_question SET name='Does the program have specific procedures for IDLH atmospheres? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=12716" target="_BLANK">Fed OSHA 1910.134(g)(3)</a>' WHERE id=1174;
UPDATE audit_question SET name='Does the program address maintenance and care of respirators?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=12716" target="_BLANK">Fed OSHA 1910.134(h)(1)(i,ii,iii,iv)</a>' WHERE id=1175;
UPDATE audit_question SET name='Does the program address proper grade of air to use and that oxygen is not used in compressed air units; cylinders meet DOT requirements, and safety issues? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=12716" target="_BLANK">Fed OSHA 1910.134(i)(1)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 18</a>' WHERE id=1177;
UPDATE audit_question SET name='Does the program address recordkeeping? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=12716" target="_BLANK">Fed OSHA 1910.134(m)(1-3)</a>' WHERE id=1179;
UPDATE audit_question SET name='Does the program specify procedures to be used for training of personnel that operate sandblasting, abrasive blasting, and hydroblasting equipment and co-workers that have the same exposure? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10607" target="_BLANK">Fed OSHA 1926.21(b)(3)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9734" target="_BLANK">Fed OSHA 1910.94</a>' WHERE id=1182;
UPDATE audit_question SET name='Does the program discuss the three (3) types of silicosis and their symptoms? ', helpText='<a href="http://www.cdc.gov/niosh/92-102.html" target="_BLANK">NIOSH Publication     92.102</a>' WHERE id=1184;
UPDATE audit_question SET name='Does the program specify methods of engineering controls? ', helpText='<a href="http://www.cdc.gov/niosh/92-102.html" target="_BLANK">NIOSH Publication     92.102</a>' WHERE id=1185;
UPDATE audit_question SET name='Does the program specify medical monitoring of workers exposed to crystalline silica? ', helpText='<a href="http://www.cdc.gov/niosh/92-102.html" target="_BLANK">NIOSH Publication     92.102</a>' WHERE id=1190;
UPDATE audit_question SET name='Does the program specify warning signs be posted? ', helpText='<a href="http://www.cdc.gov/niosh/92-102.html" target="_BLANK">NIOSH Publication     92.102</a>' WHERE id=1191;
UPDATE audit_question SET name='At a minimum, the training program must address hazards (electrical, falling objects, fall protection, use, load capacity).  Are these items addressed?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10755#1925.454(a)" target="_BLANK">Fed OSHA 1926.454(a) Training Program Content</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 23</a>' WHERE id=1194;
UPDATE audit_question SET name='Does the program address scaffold tagging? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10755" target="_BLANK">Fed OSHA 1926.454</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 23</a>' WHERE id=1196;
UPDATE audit_question SET name='Does the program address modifications by non-qualified employees?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10755" target="_BLANK">Fed OSHA 1926.454</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 23</a>' WHERE id=1197;
UPDATE audit_question SET name='Does the program address/idenity that a competent person oversee the scaffolding program and training?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10755#1926.454(b)" target="_BLANK">Fed OSHA 1926.454(b)</a>' WHERE id=4303;
UPDATE audit_question SET name='Has the company identified the tasks it performs which must only be done by or under the supervision of an employee who has Surface & Subsurface Safety Systems training? ', helpText='<a href="http://www.access.gpo.gov/nara/cfr/waisidx_05/30cfrv1_05.html" target="_BLANK">30 CFR Subpart O</a>' WHERE id=1198;
UPDATE audit_question SET name='Has the company identified job titles of employees who require Surface & Subsurface Safety Systems training? ', helpText='<a href="http://www.access.gpo.gov/nara/cfr/waisidx_05/30cfrv1_05.html" target="_BLANK">30 CFR Subpart O</a>' WHERE id=1199;
UPDATE audit_question SET name='Does the program address coordination with operators to satisfy the Subpart O requirements? ', helpText='<a href="http://www.access.gpo.gov/nara/cfr/waisidx_05/30cfrv1_05.html" target="_BLANK">30 CFR Subpart O</a>' WHERE id=1200;
UPDATE audit_question SET name='Are there provisions for a safety trenching plan?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10775" target="_BLANK">Fed OSHA 1926.651(a)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 32</a>' WHERE id=1201;
UPDATE audit_question SET name='Does the program address underground installations? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10775" target="_BLANK">Fed OSHA 1926.651(b)</a>' WHERE id=1202;
UPDATE audit_question SET name='Does the program address safe access and egress? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10775" target="_BLANK">Fed OSHA 1926.651(c)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 32</a>' WHERE id=1203;
UPDATE audit_question SET name='Does the program state that tests are to be conducted for hazardous atmospheres? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10775" target="_BLANK">Fed OSHA 1926.651(g)</a>' WHERE id=1206;
UPDATE audit_question SET name='Does the program provide for protection of employees from accumulation of water? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10775" target="_BLANK">Fed OSHA 1926.651(h)</a>' WHERE id=1207;
UPDATE audit_question SET name='Does the program provide for daily inspections made by a competent person for safety? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10775" target="_BLANK">Fed OSHA 1926.651(k)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 32</a>' WHERE id=1208;
UPDATE audit_question SET name='Does the program provide for protection of employees with regard to soil classifications? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10776" target="_BLANK">Fed OSHA 1926.652(a)(b)(1)(2)(3) (4)(c)(d)(e)(f)(g)</a>' WHERE id=1211;
UPDATE audit_question SET name='Does the program specify that maintenance of training records be kept at a local or central location and does the program address the annual MMS Marine Trash and Debris awareness training requirement? ', helpText='<a href="http://www.gomr.mms.gov/homepg/regulate/regs/ntls/ntl02-g13.html" target="_BLANK">MMS Marine Trash and Debris</a>' WHERE id=1212;
UPDATE audit_question SET name='Does the program address procedures for satisying the annual MMS Marine Trash and Debris Awareness Training requirment? ', helpText='<a href="http://www.gomr.mms.gov/homepg/regulate/regs/ntls/ntl02-g13.html" target="_BLANK">MMS Marine Trash and Debris</a>' WHERE id=1218;
UPDATE audit_question SET name='Does your program assess the hazards for water safety to include the use of ring bouys, life saving skiffs, or other life saving devices?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=10669&p_table=STANDARDS#1926.106" target="_BLANK">Fed OSHA 1926.106</a>' WHERE id=4302;
UPDATE audit_question SET name='Does the program address conditions that require a fire watch and that fire extinguishers shall be made readily avaiable? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9853#1910.252(a)(2)(iii)(A)" target="_BLANK">Fed OSHA 1910.252(a)(2)(iii)(A)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 25</a>' WHERE id=1225;
UPDATE audit_question SET name='Does the program address that fire extinguishers shall be made readily available? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9853" target="_BLANK">Fed OSHA 1910.252(a)(2)(iii)(B)</a>' WHERE id=1226;
UPDATE audit_question SET name='Does the program address maintenance of equipment? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9855" target="_BLANK">Fed OSHA 1910.254(d)</a>' WHERE id=1235;
UPDATE audit_question SET name='Does the program address Hot Work permits?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9760#1910.119(k)" target="_BLANK">Fed OSHA 1910.119(k)</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 10</a>' WHERE id=1383;
UPDATE audit_question SET name='Does the contractor have a Cadmium Awareness program?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10035" target="_BLANK">Fed OSHA 1910.1027</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10891" target="_BLANK">Fed OSHA 1926.1127</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 4</a>' WHERE id=1511;
UPDATE audit_question SET name='Does the contractor have a Hexavalent Chromium Awareness program?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=13096" target="_BLANK">Fed OSHA 1910.1026</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=13117" target="_BLANK">Fed OSHA 1926.1126</a>' WHERE id=1531;
UPDATE audit_question SET name='Has the company identified the tasks it performs which must only be done by or under the supervision of an employee who has well control training? ', helpText='<a href="http://www.msha.gov/30cfr/70.0.htm" target="_BLANK">30 CFR Subpart O</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 37</a>' WHERE id=1236;
UPDATE audit_question SET name='Has the company identified job titles of employees who require well control training? ', helpText='<a href="http://www.msha.gov/30cfr/70.0.htm" target="_BLANK">30 CFR Subpart O</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 37</a>' WHERE id=1237;
UPDATE audit_question SET name='Does the program address coordination with operators to satisfy the Subpart O requirements? ', helpText='<a href="http://www.msha.gov/30cfr/70.0.htm" target="_BLANK">30 CFR Subpart O</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 37</a>' WHERE id=1238;
UPDATE audit_question SET name='Does the program contain training requirements for operators?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9732&p_table=STANDARDS#1910.67(c)(2)(ii)" target="_BLANK">Fed OSHA 1910.67(c)(2)(ii)</a>' WHERE id=4310;
UPDATE audit_question SET name='Does the program include safe operational guidelines such as Fall Protection, Outriggers and Inspections?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9732&p_table=STANDARDS#1910.67(c)" target="_BLANK">Fed OSHA 1910.67(c)</a>' WHERE id=4311;
UPDATE audit_question SET name='Does the program address field modifications for use other than intended by the manufacturer?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_id=9732&p_table=STANDARDS#1910.67(c)(2)(i)" target="_BLANK">Fed OSHA 1910.67(c)(2)(i)</a>' WHERE id=4312;
UPDATE audit_question SET name='Does the contractor have a General Tools program?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10689" target="_BLANK">Fed OSHA 1926.301</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 25</a>' WHERE id=1313;
UPDATE audit_question SET name='Does the contractor have an Abrasive Wheel Machinery and/or other rotating equipment program?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9839 " target="_BLANK">Fed OSHA 1910.215</a>' WHERE id=1315;
UPDATE audit_question SET name='Does the contractor have a program for Powder-Actuated Tools?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10690 " target="_BLANK">Fed OSHA 1926.302(e)</a>' WHERE id=1324;
UPDATE audit_question SET name='Does the contractor have a program regarding Portable Electrical & Power Tools?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10690 " target="_BLANK">Fed OSHA 1926.302</a>' WHERE id=1327;
UPDATE audit_question SET name='Does the contractor have an Asbestos Awareness program/statement?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10862 " target="_BLANK">Fed OSHA 1926.1101</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 4</a>' WHERE id=1507;
UPDATE audit_question SET name='Does the contractor have a Benzene Awareness program/statement?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10042 " target="_BLANK">Fed OSHA 1910.1028</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 4</a>' WHERE id=1508;
UPDATE audit_question SET name='Does the contractor have a H2S (Hydrogen Sulfide) Awareness program/statement?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10790 " target="_BLANK">Fed OSHA 1926.800</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 4</a>' WHERE id=1509;
UPDATE audit_question SET name='Does the contractor have a Lead Awareness program/statement?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10641 " target="_BLANK">Fed OSHA 1926.62</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 4</a>' WHERE id=1510;
UPDATE audit_question SET name='Does the company have a written Emergency Response Plan?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 7, Sec 115</a>' WHERE id=3127;
UPDATE audit_question SET name='Does the program address keeping the Emergency Response Plan up-to-date?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 7, Sec 115 (3)</a>' WHERE id=3128;
UPDATE audit_question SET name='Does the Emergency Response Plan address identifying potential emergencies?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 7, Sec 116 (a)</a>' WHERE id=3129;
UPDATE audit_question SET name='Does the Emergency Response Plan address procedures for dealing with potential emergencies?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Sec 116</a>' WHERE id=3130;
UPDATE audit_question SET name='Does the Emergency Response Plan address the identification of, location of, and operational procedures for emergency equipment?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 7, Sec 116 (c)</a>' WHERE id=3131;
UPDATE audit_question SET name='Does the Emergency Response Plan address emergency response training?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 7, Sec 116 (d)</a>' WHERE id=3132;
UPDATE audit_question SET name='Does the Emergency Response Plan address availability of external emergency services?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 7, Sec 116 (e)</a>' WHERE id=3133;
UPDATE audit_question SET name='Does the Emergency Response Plan address alarm and emergency communication requirements?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 7, Sec 116 (g)</a>' WHERE id=3134;
UPDATE audit_question SET name='Does the Emergency Response Plan address emergency evacuation and rescue procedures? If your employees do not perform rescue, your program must specify the external emergency services that will be used in the event rescue is required.', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 7, Sec 116 (i)</a>' WHERE id=3135;
UPDATE audit_question SET name='Does the contractor have an Employee Evaluation program?', helpText='<a href="http://www.dir.ca.gov/dosh/dosh_publications/IIPP.html#25" target="_BLANK"></a>' WHERE id=1291;
UPDATE audit_question SET name='Does the contractor have a program to ensure that hazards will be corrected in a timely manner?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=OSHACT&p_id=3359" target="_BLANK">General Duty Clause Section 5(a)(1)</a> <a href="http://apps.leg.wa.gov/wac/default.aspx?cite=296-305-01503" target="_BLANK">WAC 296-24-202</a> <a href="http://www.lni.wa.gov/Safety/Rules/Policies/Topic/wiims/964a.asp" target="_BLANK">WAC 296-24-040</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 2</a>' WHERE id=1311;
UPDATE audit_question SET name='Does the contractor have a program for the use and storage of Compressed Air Systems? To include Air nozzles, Air Hose, Air connectors, etc.', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10690 " target="_BLANK">Fed OSHA 1926.302 (b)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10694 " target="_BLANK">Fed OSHA 1926.306</a>' WHERE id=1318;
UPDATE audit_question SET name='Does the contractor have a program for Concrete/Masonry Construction?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10777 " target="_BLANK">Fed OSHA 1926.700 - 706</a>' WHERE id=1319;
UPDATE audit_question SET name='Does the contractor have a program for Elevated Work Platforms and Aerial Devices?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9732 " target="_BLANK">Fed OSHA 1910.67</a>' WHERE id=1320;
UPDATE audit_question SET name='Does the contractor have a program for Ladder Inspection & Use?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10839" target="_BLANK">Fed OSHA 1926.1053</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9717 " target="_BLANK">Fed OSHA 1910.25 - 27</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 18</a>' WHERE id=1323;
UPDATE audit_question SET name='Does the contractor have a statement for Access to Employee Exposure & Medical Records?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10027 " target="_BLANK">Fed OSHA 1910.1020</a>' WHERE id=1328;
UPDATE audit_question SET name='Does the contractor have an Emergency Response/Evacuation program/plan?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9726 " target="_BLANK">Fed OSHA 1910.38</a> <a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Part 7</a>' WHERE id=1329;
UPDATE audit_question SET name='Does the contractor have a Drug and Alcohol program?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=INTERPRETATIONS&p_id=22577" target="_BLANK">Fed OSHA Letter of Interpretation</a> <a href="http://apps.leg.wa.gov/WAC/default.aspx?cite=260-34-020" target="_BLANK">WAC 260-34-020 </a>' WHERE id=1330;
UPDATE audit_question SET name='Provide your program that discusses the process you use to investigate an Accident, Incident, and/or Near Miss.', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=OSHACT&p_id=3359" target="_BLANK">General Duty Clause 5(a)(1)</a> <a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=9760#1910.119(m)" target="_BLANK">Fed OSHA 1910.119(m)</a>' WHERE id=4309;
UPDATE audit_question SET name='Does the program address performing a hazard assessment before manually lifting and handling a load?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Sec 210 (1)</a>' WHERE id=3136;
UPDATE audit_question SET name='Does the program address the use of mechanized equipment for material handling?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Sec 208 (1)</a>' WHERE id=3137;
UPDATE audit_question SET name='Does the program address that workers are provided ergonomics training?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 14 Sec 211.1</a>' WHERE id=3138;
UPDATE audit_question SET name='Does the program address when guarding is required to protect workers?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 22, Sec 310</a>' WHERE id=3139;
UPDATE audit_question SET name='Does the program address that tampering with safeguards is prohibited?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 22, Sec 311</a>' WHERE id=3140;
UPDATE audit_question SET name='Does the company have a written Workplace Violence program?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Code Part 27 Sec 390</a>' WHERE id=3141;
UPDATE audit_question SET name='Does the program address that victims of workplace violence are advised to consult a health professional?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Code Part 27 Sec 392</a>' WHERE id=3142;
UPDATE audit_question SET name='Does the program address worker education on workplace violence?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Code Part 27 Sec 391</a>' WHERE id=3143;
UPDATE audit_question SET name='Does the company have a written Working Alone program?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Code Part 28 Sec 393</a>' WHERE id=3144;
UPDATE audit_question SET name='Does the program address that workers must carry a cellular phone or electronic monitoring device at all times while working alone?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Code Part 28 Sec 394 (1)</a>' WHERE id=3145;
UPDATE audit_question SET name='Does the program address an alternate means of monitoring workers working alone when electronic communication is not practicable?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Code Part 28 Sec 394 </a>' WHERE id=3146;
UPDATE audit_question SET name='Does the program address methods to provide employees information, training, and documentation of training?', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10099#1910.1200(h)(1)" target="_BLANK">Fed OSHA 1910.1200(h)(1)(2)(3)</a>' WHERE id=1045;
UPDATE audit_question SET name='Does the program specify that a written hazard communication program be developed, implemented, & maintained at each workplace and has the position been listed who has full authority for its implementation & execution? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10099#1910.1200(e)(1)" target="_BLANK">Fed OSHA 1910.1200(e)(1)</a>' WHERE id=1046;
UPDATE audit_question SET name='Does the program mandate the employer to maintain a list of hazardous chemicals on the job site and specify where MSDS are to be maintained?  ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10099#1910.1200(e)(1)(i)" target="_BLANK">Fed OSHA 1910.1200(e)(1)(i)</a>' WHERE id=1047;
UPDATE audit_question SET name='Does the program address multi-employer job sites and/or multi work sites? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10099" target="_BLANK">Fed OSHA 1910.1200(e)(2)(4)(5)</a>' WHERE id=1049;
UPDATE audit_question SET name='Does the program address the use & care of labels and other forms of warning and insure that the labels are not defaced or removed? ', helpText='<a href="http://www.osha.gov/pls/oshaweb/owadisp.show_document?p_table=STANDARDS&p_id=10099#1910.1200(f)(8)" target="_BLANK">Fed OSHA 1910.1200(f)(8)</a>' WHERE id=1050;
UPDATE audit_question SET name='Does the program address worker exposure to harmful substances? The program must demonstrate that all requirements listed in Additional Comments are met.', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 4, Sec 16 (1) (2)</a>' WHERE id=3117;
UPDATE audit_question SET name='Does the program address that workers may not be exposed to a concentration of a harmful substance that exceeds its ceiling limits?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 4, Sec 16 (3.1)</a>' WHERE id=3118;
UPDATE audit_question SET name='Does the program address education and training for workers?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 4, Sec 16 (2), Sec 21</a>' WHERE id=3119;
UPDATE audit_question SET name='Does the program address emergency equipment for chemical exposure?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 4, Sec 24</a>' WHERE id=3120;
UPDATE audit_question SET name='Does the company have a written Workplace Hazardous Materials Information System (WHMIS) program?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 29, Sec 395</a>' WHERE id=3147;
UPDATE audit_question SET name='Does the program address that hazardous waste is labeled and that workers are trained on safe handling of hazardous waste? If your company does not handle hazardous waste, please state this in your written program.', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 29, Sec 396</a>' WHERE id=3148;
UPDATE audit_question SET name='Does the program address that all workers who work with or near controlled products are provided WHMIS training?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 29, Sec 397</a>' WHERE id=3149;
UPDATE audit_question SET name='Does the program address that all controlled products are labelled with either a supplier label or a work site label?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 29, Sec 398</a>' WHERE id=3150;
UPDATE audit_question SET name='Does the program address that Material Safety Data Sheets (MSDS) are readily available for all controlled products used at the worksite?', helpText='<a href="http://www.employment.alberta.ca/SFW/3969.html" target="_BLANK">AB OHS Guide Part 29, Sec 404,407</a>' WHERE id=3151;
UPDATE audit_question SET name='Has the Contractor uploaded the proper OSHA 300 form for each of the last 3 years in the Annual Updates?', helpText='<a href="http://apps.leg.wa.gov/WAC/default.aspx?cite=296-27-02105" target="_BLANK">WAC 296-27-02105</a>' WHERE id=1512;
UPDATE audit_question SET name='Does the information provided on the OSHA logs match what the contractor has inputted in the Annual Updates?', helpText='<a href="http://apps.leg.wa.gov/WAC/default.aspx?cite=296-27-02105" target="_BLANK">WAC 296-27-02105</a>' WHERE id=1513;