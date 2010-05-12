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

/** Adding existing widgets to CSR users**/
insert into widget_user 
select null,widgetID,959,expanded, 
wu.column,sortOrder,customConfig from widget_user wu 
where 
(userid = 941 and widgetid in (2,16,18))
or (userid = 616 and widgetid = 1);

insert into widget
(widgetID, caption, widgetType, url, chartType)
values
(null, 'Outstanding Submitted Audits', 'Html', 'SubmittedAuditsAjax.action',null),
(null, 'Outstanding Rejected Policies', 'Html', 'RejectedPoliciesAjax.action',null),
(null, 'Flag Color Count by Category', 'Chart', 'ChartXMLFlagCategoryCount.action','ScrollStackedColumn2D');

/** Make sure that the widgetIDs are correct! **/
insert into widget_user
(id, widgetID, userID, column, sortOrder)
values
(null, 26, 959, 2, 30),
(null, 28, 959, 1, 30);
