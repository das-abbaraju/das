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

-- PICS-727: Begin --
update generalcontractors set forcedBy = 776 where id = 5716;
update generalcontractors set forcedBy = 987 where id in (748, 9867, 11077, 11078, 12815, 23831);
update generalcontractors set forcedBy = 556 where id = 12764;
update generalcontractors set forcedBy = 2173 where id in (16317, 17795, 17797, 17798);
update generalcontractors set forcedBy = 2175 where id in (17842, 17843, 17844);
update generalcontractors set forcedBy = 8597 where id = 28914;
update generalcontractors set forcedBy = 11429 where id in (34391, 35382, 36494, 36864, 40366);
update generalcontractors set forcedBy = 10569 where id = 35912;
-- PICS-727: END --

-- PICS-678: Begin --
update contractor_audit ca set ca.closingAuditorID = ca.auditorID where ca.closingAuditorID is null and ca.auditorID not in (10600,910,902);
update contractor_audit ca set ca.closingAuditorID = 1029 where ca.closingAuditorID is null and ca.auditorID = 10600;
update contractor_audit ca set ca.closingAuditorID = 9615 where ca.closingAuditorID is null and ca.auditorID = 910;
update contractor_audit ca set ca.closingAuditorID = 11503 where ca.closingAuditorID is null and ca.auditorID = 902;
-- PICS-678: END --

/* Deactive unused assessment centers except for OQSG, and set NACE and NCCER to pending */
update accounts set status = 'Deactivated' where type = 'Assessment' and name !='OQSG';

update accounts set status = 'Pending' where id = 11069 or id = 11087;

/* Set Contractor logo to null where it is blank or No */
update contractor_info set logo_file = NULL where logo_file = '' or logo_file = 'No';

-- Add employee list to all PICS Admins (Operator Admins?)
insert into useraccess 
(accessID, userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
values (NULL, 10, 'EmployeeList', 1, 1, 1, 1, NOW(), 941);
--, (NULL, 1553, 'EmployeeList', 1, 0, 0, 1, NOW(), 941);

-- Add TRIR Reports to all PICS Admins
insert into useraccess
(accessID, userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
values (NULL, 10, 'TRIRReport', 1, null, null, 1, NOW(), 941);

-- PICS-775 --
insert into token(tokenName, listType, velocityCode) values('ClosingAuditorName', 'Audit', '${audit.closingAuditor.name}');
insert into token(tokenName, listType, velocityCode) values('ClosingAuditorPhone', 'Audit', '${audit.closingAuditor.phone}');
insert into token(tokenName, listType, velocityCode) values('ClosingAuditorFax', 'Audit', '${audit.closingAuditor.fax}');
insert into token(tokenName, listType, velocityCode) values('ClosingAuditorEmail', 'Audit', '${audit.closingAuditor.email}');

insert into token(tokenName, listType, velocityCode) values('AuditorPhone', 'Audit', '${audit.auditor.phone}');
insert into token(tokenName, listType, velocityCode) values('AuditorFax', 'Audit', '${audit.auditor.fax}');
insert into token(tokenName, listType, velocityCode) values('AuditorEmail', 'Audit', '${audit.auditor.email}');

update pqfcategories set applyOnQID = 2064, applyOnAnswer = 'Yes' where id = 151; -- Osha Audit
update pqfcategories set applyOnQID = 2065, applyOnAnswer = 'Yes' where id = 157; -- MSHA
update pqfcategories set applyOnQID = 2033, applyOnAnswer = 'Yes' where id = 152; -- EMR
update pqfcategories set applyOnQID = 2033, applyOnAnswer = 'No' where id = 159; -- Loss Run
update pqfcategories set applyOnQID = 3546, applyOnAnswer = 'Yes' where id = 278; -- Citations

-- PICS-595: Waiting On
insert into widget
(widgetID, caption, widgetType, synchronous, url, requiredPermission, chartType)
values
(null, 'Waiting On PICS', 'Html', 0, 'WaitingOnAjax.action', null, null);

insert into widget_user
(id, widgetID, userID, expanded, widget_user.column, sortOrder, customConfig)
values
(null, 33, 959, 1, 2, 37, null);

-- PICS-788, Operator Admin and Operator Basic users
insert into useraccess 
(accessID, userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
values
(null, 1553, 'TRIRReport', 1, null, null, 1, now(), 941),
(null, 1554, 'TRIRReport', 1, null, null, 1, now(), 941);