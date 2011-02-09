-- PICS-1797 - Use a better label for annual stats
update flag_criteria set label = REPLACE(label, right(label, 3), '') where label like '% ''%';

update accounts set industry = 'WoodProducts'
where name like 'Roseburg Forest Products%'
and type in ('Operator','Corporate');

-- PICS-1496
insert into widget 
(caption, widgetType, synchronous, url)
values
('Flag Changes', 'Html', 0, 'FlagChangesWidgetAjax.action');

insert into widget_user 
(widgetID, userID, expanded, `column`, sortOrder)
values
(34, 959, 1, 1, 20);

delete from contractor_tag where tagid in (select id from operator_tag where tag = '');
delete from audit_category_rule where tagid in (select id from operator_tag where tag = '');
delete from audit_type_rule where tagid in (select id from operator_tag where tag = '');
delete from operator_tag where tag = '';

-- Effective Dates for Contractor Audits
update contractor_audit set effectiveDate = null;

update contractor_audit ca
join contractor_audit_operator cao on cao.auditID = ca.id
set ca.effectiveDate = min(cao.statusChangedDate) 
where cao.status IN ('Submitted')
and ca.auditTypeID not in (1, 11)
group by ca.id;

update contractor_audit set effectiveDate = concat(auditFor,'-01-01')
where auditTypeID = 11;

alter table audit_type drop column emailTemplateID;
