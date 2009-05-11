/**
update pqfquestions set isVisible = CASE isVisible WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set hasRequirement = CASE hasRequirement WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isGroupedWithPrevious = CASE isGroupedWithPrevious WHEN 2 THEN 1 ELSE 0 END;
update pqfquestions set isRedFlagQuestion = CASE isRedFlagQuestion WHEN 2 THEN 1 ELSE 0 END;
*/
UPDATE audit_type set classType = 'PQF'
where auditName like 'PQF%';

update flagoshacriteria set lwcrHurdleType = 'None', trirHurdleType = 'None', fatalitiesHurdleType = 'None';

update flagoshacriteria set lwcrHurdleType = 'Absolute' where flagLwcr = 'Yes';
update flagoshacriteria set trirHurdleType = 'Absolute' where flagTrir = 'Yes';
update flagoshacriteria set fatalitiesHurdleType = 'Absolute' where flagFatalities = 'Yes';

/**
 * update the NAICS from the pqfdata for the existing contractors
 * 
 */
create TEMPORARY table temp_naics
(id Mediumint(8), naics varchar(10));

insert into temp_naics 
select a.id, SUBSTRING_INDEX(pd.answer,',',1) from accounts a
join contractor_audit ca on ca.conid = a.id
Left join pqfdata pd on pd.id = ca.id
where a.type = 'Contractor'
and ca.audittypeId = 1
and pd.answer != null or pd.answer != ''
and pd.questionid = 57;
 
select * from temp_naics;

update accounts a, temp_naics t set a.naics = t.naics
where a.id = t.id;

update accounts a set naics = NULL
where naics = 'NA' or naics = 'N/A';

update accounts a set naics = 562910
where id = 7060;

/**
 * Added open Notes widget to all users
 */
insert into widget values
('Open Notes', 'Html',0,'UserOpenNotesAjax.action',null, null);

insert into widget_user values
(newwidgetid, 941, 1,1,5, null);
insert into widget_user values
(newwidgetid, 910, 1,1,5, null);
insert into widget_user values
(newwidgetid, 616, 1,1,5, null);
insert into widget_user values
(newwidgetid, 646, 1,1,5, null);