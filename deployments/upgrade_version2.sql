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

-- PICS-788, Operator Admin and Operator Basic users
insert into useraccess 
(accessID, userID, accessType, viewFlag, editFlag, deleteFlag, grantFlag, lastUpdate, grantedByID)
values
(null, 1553, 'TRIRReport', 1, null, null, 1, now(), 941),
(null, 1554, 'TRIRReport', 1, null, null, 1, now(), 941);
