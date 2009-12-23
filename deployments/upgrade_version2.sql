/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
where os.applicable = 1
and pcd.applies = 'No'
and pcd.catID = 151; 

select os.applicable, os.auditid, pcd.applies from osha_audit os 
join pqfcatdata pcd on pcd.auditid = os.auditid
join contractor_audit ca on ca.id = os.auditid 
where os.applicable = 0
and pcd.applies = 'Yes'
and pcd.catID = 151;
**/


-- Change countries to ISO codes on accounts table
update accounts set country = 'CA' where country = 'Canada';

update accounts set country = 'US' where country = 'USA';

update accounts set country = null where country like '%- country -%';

update accounts a join country c on a.country = c.english set a.country = c.isoCode where a.country not in ('us', 'ca');

-- fix countries in the pqfdata table
-- verify that all answers are valid countries
select distinct answer 
from pqfdata d 
	join pqfquestions q on d.questionid = q.id 
where q.questiontype = 'country';

update pqfdata d 
	join pqfquestions q on d.questionid = q.id
set d.answer = 'US'
where q.questionType = 'Country' and d.answer = 'USA';

update pqfdata d 
	join pqfquestions q on d.questionid = q.id
set d.answer = 'CA'
where q.questionType = 'Country' and d.answer = 'Canada';

update pqfdata d 
	join pqfquestions q on d.questionid = q.id
	left join country c on d.answer = c.english
set d.answer = c.isoCode
where q.questionType = 'Country' and d.answer not in ('US', 'CA');

-- end fix countries

-- create all en_US pqfquestions
insert into pqfquestion_text (questionID, locale, question, requirement, createdBy, updatedBy, creationDate, updateDate)
select id, 'en', question, requirement, 2357, 2357, now(), now() from pqfquestions;

-- Added a new widget for Operator Flag History
insert into widget values
(null,"Operator Flag History","Chart",0,"OperatorFlagHistoryAjax.action",null,"ScrollStackedColumn2D");

insert into widget_user values
(null,27,616,1,1,10,null);
