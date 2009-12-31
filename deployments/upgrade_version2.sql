USE `pics`;

/* Alter table in target */
ALTER TABLE `contractor_info` 
	ADD COLUMN `tradesSelf` varchar(500)  COLLATE latin1_swedish_ci NULL after `lwcrAverage`, 
	ADD COLUMN `tradesSub` varchar(500)  COLLATE latin1_swedish_ci NULL after `tradesSelf`, 
	DROP COLUMN `oldriskLevel`;

/* Alter table in target */
DROP TABLE `country`;

/* Alter table in target */
ALTER TABLE `generalcontractors` 
	ADD COLUMN `processCompletion` datetime   NULL after `updateDate`;

/* Alter table in target */
ALTER TABLE `osha_audit` 
	ADD COLUMN `firstAidInjuries` smallint(5) unsigned   NULL DEFAULT '0' after `neer`, 
	ADD COLUMN `modifiedWorkDay` smallint(5) unsigned   NULL DEFAULT '0' after `firstAidInjuries`;


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

update accounts a join ref_country c on a.country = c.english set a.country = c.isoCode where a.country not in ('us', 'ca');

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
	left join ref_country c on d.answer = c.english
set d.answer = c.isoCode
where q.questionType = 'Country' and d.answer not in ('US', 'CA');

-- end fix countries

-- set empty string states to null
update accounts set state = null where state = "";

-- same for billing states
update contractor_info set billingstate = null where billingstate = "";

-- convert old states from english to isocodes
update contractor_info c join ref_state s on c.billingstate = s.english set c.billingstate = s.isocode;

-- fix the one bad spelling of PA, SC, NC
update contractor_info set billingstate = 'PA' where billingstate = 'Pennsylvan';
update contractor_info set billingstate = 'SC' where billingstate = 'SOUTH CARO';
update contractor_info set billingstate = 'NC' where billingstate = 'North Caro';
update contractor_info set billingstate = 'AB' where billingstate = 'Alberta, C';
update contractor_info set billingstate = 'TN' where billingstate = 'TN.';
update contractor_info set billingstate = 'AK' where billingstate = 'Ak.';

-- fix lowercase states
update contractor_info set billingstate = upper(billingstate);

-- fixes for contractor_audit bad states
update contractor_audit set state = null where state = "";
update contractor_audit set state = upper(state);

update contractor_audit set country = null where country = "";
update contractor_audit set country = 'US' where country = 'USA';

-- create all en_US pqfquestions
insert into pqfquestion_text (questionID, locale, question, requirement, createdBy, updatedBy, creationDate, updateDate)
select id, 'en', question, requirement, 2357, 2357, now(), now() from pqfquestions;

-- Added a new widget for Operator Flag History
insert into widget values
(null,"Operator Flag History","Chart",0,"OperatorFlagHistoryAjax.action",null,"ScrollStackedColumn2D");

insert into widget_user values
(null,27,616,1,1,10,null);

-- move the WCB Category to Annual Update  
update pqfcategories set auditTypeID = 11, number = 7 
where id = 210;

update audit_type set renewable = 0;
update audit_type set renewable = 1 where classType = 'PQF';
update audit_type set renewable = 1 where id = 31; -- EBIX

update generalcontractors set processCompletion = '2009-01-01'
where exists (select * from flags where flags.conID = generalcontractors.subID and flags.opID = generalcontractors.genID and flags.waitingOn = 0);
