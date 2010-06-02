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
create TEMPORARY table temp_contractor_info
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

update widget_user set sortOrder = 30 where widgetID = 29 and userID = 959;

insert into widget (widgetID, caption, widgetType, url)
values (null, 'Pending Welcome Calls', 'Html', 'WelcomeCallAjax.action');

insert into widget_user (id, widgetID, userID, widget_user.column, sortOrder)
values (null, 32, 959, 2, 20);

-- database diff
Trevor: ALTER TABLE `audit_type`
ADD COLUMN `opID` mediumint(9) NULL after `renewable`, COMMENT='';

ALTER TABLE `contractor_info`
ADD COLUMN `agreementDate` datetime NULL after `tradesSub`,
ADD COLUMN `agreedBy` int(11) NULL after `agreementDate`, COMMENT='';

ALTER TABLE `email_queue`
ADD COLUMN `viewableBy` int(10) NULL after `html`, COMMENT='';

ALTER TABLE `employee`
ADD COLUMN `twicExpiration` date NULL after `phone`;

ALTER TABLE `employee_site`
ADD COLUMN `orientationDate` date NULL after `expirationDate`,
ADD COLUMN `orientationExpiration` date NULL after `orientationDate`;

ALTER TABLE `generalcontractors`
CHANGE `flag` `flag` enum('Red','Amber','Green','Clear') COLLATE latin1_swedish_ci NOT NULL DEFAULT 'Red';

alter table flag_criteria_operator drop column `percentAffected`;
