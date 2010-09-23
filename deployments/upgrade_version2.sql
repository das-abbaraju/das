/** Update the requiresOQ for all contractors
 * we don't want to run this yet 
update accounts set requiresOQ = 1
where id in (select distinct conid from contractor_audit ca
join pqfdata pd on ca.id = pd.auditid
where pd.questionid = 894
and pd.answer = 'Yes');
**/

delete from pqfsubcategories where categoryID in (select id from pqfcategories WHERE auditTypeID = 18);
delete from pqfcategories where auditTypeID = 18;
delete from audit_category where auditTypeID = 18;
delete from audit_type where id = 18;

-- PICS-595: Waiting On
insert into widget
(widgetID, caption, widgetType, synchronous, url, requiredPermission, chartType)
values
(null, 'Waiting On PICS', 'Html', 0, 'WaitingOnAjax.action', null, null);

insert into widget_user
(id, widgetID, userID, expanded, widget_user.column, sortOrder, customConfig)
values
(null, 33, 959, 1, 2, 37, null);

-- Start Canadian annual update conversion --
update audit_type set auditName = 'Annual Update US' where id = 11;

update flag_criteria set label = 'US Annual Update' where id in (142, 183);

update audit_operator set auditTypeID = 18 where opID in (7901, 9804, 9986, 10118) and auditTypeID  = 11;

update flag_criteria_operator set criteriaID = 132 where opID in (7901, 9804, 9986, 10118) and criteriaID = 142;

insert into contractor_audit
select null, 18, conID, ca.creationDate, ca.createdBy, now(), 941, auditStatus, expiresDate, auditorID, assignedDate, 
	scheduledDate, completedDate, closedDate, requestedByOpID, auditLocation, percentComplete, percentVerified, 
	contractorConfirm, auditorConfirm, manuallyAdded, auditFor, needsCamera, lastRecalculation, score, closingAuditorID, 
	contractorContact, phone, phone2, address, address2, city, state, zip, country, latitude, longitude, paidDate
from operators o 
join generalcontractors gc on o.id = gc.genID
join contractor_audit ca on ca.conID = gc.subID and ca.auditTypeID = 11 and ca.auditStatus != 'Expired'
where o.inheritAudits in (7901, 9804, 9986, 10118);

create table temp_annual_updates as
select ca1.id id11, min(ca2.id) id18 from contractor_audit ca1
join contractor_audit ca2 on ca1.conID = ca2.conID and ca1.auditFor = ca2.auditFor
where ca1.auditTypeID = 11 and ca2.auditTypeID = 18
group by ca1.id;

update audit_type set classType = 'AnnualUpdate' where id = 11;

update pqfcategories set audittypeid = 18 where id in (158, 210);

update pqfquestions set subCategoryID = 485, countries = null, isVisible = 'No' where id in (2967, 2066);

insert into pqfquestions (subCategoryID, number, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate, hasRequirement, okAnswer, isRequired, isVisible, dependsOnQID, dependsOnAnswer, questionType, title, columnHeader, isGroupedWithPrevious, isRedFlagQuestion, link, linkText, linkURL1, linkText1, linkURL2, linkText2, linkURL3, linkText3, linkURL4, linkText4, linkURL5, linkText5, linkURL6, linkText6, uniqueCode, showComment, riskLevel, helpPage, countries)
select 485, number, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate, hasRequirement, okAnswer, isRequired, isVisible, dependsOnQID, dependsOnAnswer, questionType, title, columnHeader, isGroupedWithPrevious, isRedFlagQuestion, link, linkText, linkURL1, linkText1, linkURL2, linkText2, linkURL3, linkText3, linkURL4, linkText4, linkURL5, linkText5, linkURL6, linkText6, uniqueCode, showComment, riskLevel, helpPage, countries
from pqfquestions
where id in (2447, 2448);

update pqfdata d, temp_annual_updates t
set d.auditID = t.id18
where d.auditID = t.id11 and d.questionID in (2967, 2066);

insert into pqfquestion_text (questionID, locale, question, requirement, createdBy, updatedBy, creationDate, updateDate)
select q2.id, t1.locale, t1.question, t1.requirement, t1.createdBy, t1.updatedBy, t1.creationDate, t1.updateDate from pqfquestions q1
join pqfquestion_text t1 on q1.id = t1.questionID
join pqfquestions q2 on q2.number = q1.number and q2.subcategoryid = 485
where q1.id in (2447, 2448);

-- PICS-415
update `token` set `velocityCode`='${flagColor}' where `tokenID`='8';

-- PICS-630/805 --
update accounts set nameIndex = replace(nameIndex, ' ', '');

-- PICS-788, PICS-42: Update TRIR report to Incidence Report (Graph), change permission on Incidence Rate Report
insert into pics_stage.useraccess 
(accessID, userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
select null, ua.userID, 'TRIRReport', ua.viewFlag, ua.editFlag, ua.deleteFlag, ua.grantFlag, now(), 941
from useraccess ua where ua.accessType = 'FatalitiesReport';

-- Audit Changes
insert into audit_category
select id, auditTypeID, null parent, 
category name, number, numRequired, numQuestions, null helpText, 
createdBy, updatedBy, creationDate, updateDate, false pageBreak, 
null legacyID
from pqfcategories;


insert into audit_category 
select null,pc.auditTypeID,ps.categoryID, ps.subCategory,
ps.number,-1,-1,ps.helpText,ps.createdBy,ps.updatedBy,ps.creationDate, 
ps.updateDate, false,ps.id 
from pqfsubcategories ps
join pqfcategories pc on ps.categoryID = pc.id;


insert into audit_question
select q.id, c.id categoryID, q.number, t.question name, q.createdBy, q.updatedBy, q.creationDate, q.updateDate, q.effectiveDate, q.expirationDate, q.questionType, (q.hasRequirement = 'Yes') hasRequirement, q.okAnswer, (q.isRequired = 'Yes') required, q.dependsOnQID, q.dependsOnAnswer, null visibleQuestion, null visibleAnswer, q.columnHeader, q.uniqueCode, q.title, (q.isGroupedWithPrevious = 'Yes') groupedWithPrevious, (q.isRedFlagQuestion = 'Yes') flaggable, q.showComment, q.riskLevel, q.helpPage, t.requirement
from `pics_yesterday`.pqfquestions q
  join `pics_yesterday`.pqfquestion_text t
    on q.id = t.questionID
      and t.locale = 'en'
  join audit_category c
    on c.legacyID = q.subCategoryID;
    
-- Move questions up a level where there is only a single subcategory
update audit_question q
  join (select
          p.id    PID,
          c.id    CID
        from audit_category p
          join audit_category c
            on p.id = c.parentID
        group by p.id
        having count(c.id) = 1) t
set q.categoryID = t.pid
where q.categoryID = t.cid;

-- Delete all Subcategoryies from the previous query. This cannot be run in MySQL
-- the result can be pasted in
delete from audit_question
where id in (update audit_question q
  join (select
          c.id    CID
        from audit_category p
          join audit_category c
            on p.id = c.parentID
        group by p.id
        having count(c.id) = 1);
        
-- Update existing accounts (operator, contractor) to use onsite services
-- Should we do this for all of these accounts?
update accounts
set onsiteServices = 1
where type in ('Contractor', 'Operator');

-- Convert caos to insurance policy categories and questions

create table temp_cao_conversion as
select distinct ao.opID, ao.auditTypeID, ao.help
from accounts a
join operators o on a.id = o.id
join audit_operator ao on ao.canSee = 1 and ao.opID = o.inheritInsurance
join audit_type aType on aType.id = ao.auditTypeID and aType.classType = 'Policy'
where a.status in ('Active','Pending') and a.type = 'Operator';

update temp_cao_conversion set help = '' where help is null;

insert into audit_category (auditTypeID, name, number, numRequired, numQuestions, createdBy, updatedBy, creationDate, updateDate, legacyID)
select auditTypeID, a.name, 3, 2, 2, 941, 941, now(), now(), t.id
from temp_cao_conversion t
join accounts a on a.id = t.opID;

insert into audit_category_rule 
(priority, include, catID, auditTypeID, opID, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate, level)
select 315, 1, ac.id, t.auditTypeID, opID, 941, 941, now(), now(), now(), '4000-01-01', 3
from temp_cao_conversion t
join audit_category ac on ac.legacyID = t.id;

insert into audit_question 
(categoryID, number, name, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate, questionType, hasRequirement, required, columnHeader, groupedWithPrevious, flaggable, showComment)
select ac.id, 1, 'Upload a Certificate of Insurance or other supporting documentation for this policy.', 941, 941, now(), now(), now(), '4000-01-01', 'FileCertificate', 0, 1, 'Certificate', 0, 0, 0
from temp_cao_conversion t
join audit_category ac on ac.legacyID = t.id;

insert into audit_question 
(categoryID, number, name, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate, questionType, hasRequirement, required, columnHeader, groupedWithPrevious, flaggable, showComment)
select ac.id, 2, concat('This insurance policy complies with all additional ', trim(a.name), ' requirements. ', t.help), 941, 941, now(), now(), now(), '4000-01-01', 'Yes/No', 0, 1, 'Certificate', 0, 0, 0
from temp_cao_conversion t
join audit_category ac on ac.legacyID = t.id
join accounts a on t.opID = a.id;


-- insert the Audit Category Data for subcategories
select null,acd.auditID,acs.id,acd.requiredCompleted,acd.numRequired,acd.numAnswered, 
acd.applies,acd.percentCompleted,acd.percentVerified,acd.percentClosed,acd.override, 
acd.score,acd.scoreCount,acd.createdBy,acd.updatedBy,acd.creationDate,acd.updateDate
from audit_category acp
join audit_category acs on acp.id = acs.parentID
join audit_cat_data acd on acd.categoryID = acp.id
where acp.parentID is null;

-- TODO insert translations
select 
from audit_question;



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

update audit_type set workflowID = 1;
update audit_type set workflowID = 2 where hasRequirements = 1;
update audit_type set workflowID = 3 where classType = 'Policy';
update audit_type set workflowID = 4 where id = 1;
update audit_type set workflowID = 5 where id = 11;

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

/*
 *  Dropping Tables and Columns
 */
alter table `pics_alpha`.`audit_type` drop column `mustVerify`, drop column `hasRequirements`;

alter table `pics_alpha`.`contractor_audit` drop column `auditStatus`, drop column `completedDate`, drop column `closedDate`, drop column `percentComplete`, drop column `percentVerified`;

alter table `pics_alpha`.`contractor_audit_operator` drop column `submittedDate`, drop column `completedDate`, drop column `approvedDate`, drop column `incompleteDate`, drop column `notes`, drop column `reason`, drop column `valid`, drop column `certificateID`, drop column `statusChangedDate`;

drop table `pics_alpha`.`pqfcatdata`;
alter table `pics_alpha`.`pqfcategories` drop foreign key  `FK_pqfcategories` ;
alter table `pics_alpha`.`pqfsubcategories` drop foreign key  `FK_pqfsubcategories` ;
drop table `pics_alpha`.`pqfcategories`;
drop table `pics_alpha`.`pqfsubcategories`;
drop table `pics_alpha`.`pqfopmatrix`;
drop table `pics_alpha`.`pqfquestions`;
drop table `pics_alpha`.`audit_operator`;
drop table `pics_alpha`.`desktopmatrix`;
