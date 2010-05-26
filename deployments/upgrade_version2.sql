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

-- PICS-617  *** Begin ***
create TEMPORARY table temp_contractor_info;

insert into temp_contractor_info
select c.id, min(u.id) as userid from users u
join contractor_info c on c.id = u.accountid
group by c.id;

update contractor_info c, temp_contractor_info t
set c.agreedBy = t.userid
where c.id = t.id;

update contractor_info c
join accounts a on c.id = a.id
set c.agreementDate = a.creationDate;
-- PICS-617  *** End ***


update email_template set allowsVelocity = 1, html = 1, recipient = 'Admin' where id = 107;
insert into `app_properties` (`property`, `value`) values('subscription.ContractorAdded','1');

update widget_user set sortOrder = 10 where userID = 959 and widgetID = 26;