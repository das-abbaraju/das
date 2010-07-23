/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
**/

/** Update the requiresOQ for all contractors
 * we don't want to run this yet 
update accounts set requiresOQ = 1
where id in (select distinct conid from contractor_audit ca
join pqfdata pd on ca.id = pd.auditid
where pd.questionid = 894
and pd.answer = 'Yes');
**/

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