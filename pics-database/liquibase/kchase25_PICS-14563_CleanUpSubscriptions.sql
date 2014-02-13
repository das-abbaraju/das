--liquibase formatted sql

--changeset kchase:25

--  delete duplicate
CREATE TEMPORARY TABLE myDeletes
AS
select e1.id from email_subscription as e1
join email_subscription e2 on e2.`subscription` = e1.`subscription` and e2.`userID` = e1.`userID`
where e2.id > e1.id and e2.`subscription` != 'DynamicReports'
;

DELETE email_subscription
FROM
	email_subscription
JOIN
	myDeletes
ON	myDeletes.id	= email_subscription.id
;

-- update invalid reports
update ignore email_subscription
set reportID=490
where reportID=108
;

