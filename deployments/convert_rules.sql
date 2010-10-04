insert into audit_category_rule 
(priority, include, catID, auditTypeID, opID, createdBy, updatedBy, creationDate, updateDate, effectiveDate, expirationDate, level)
select 315, 1, ac.id, t.auditTypeID, opID, 941, 941, now(), now(), now(), '4000-01-01', 3
from temp_cao_conversion t
join audit_category ac on ac.legacyID = t.id;



drop table temp_pqfopmatrix_full;

create table temp_pqfopmatrix_full as
select c.catID, o.opID, r.riskLevel, case when t.id is null then 0 else 1 end onFlag, 0 include
from (select distinct catID FROM pqfopmatrix m join pqfcategories t on m.catID = t.id) c
join (select distinct inheritAuditCategories opID FROM accounts a join operators using (id) WHERE a.status in ('Active','Pending') and a.type = 'Operator') o
join (select distinct riskLevel FROM pqfopmatrix) r
LEFT join pqfopmatrix t on t.catID = c.catID and t.opID = o.opID and t.riskLevel = r.riskLevel;

delete from audit_category_rule where expirationDate < now();
delete from audit_category_rule where effectiveDate < '2001-01-01';
insert into audit_category_rule (auditTypeID, catID, opID, risk, include, effectiveDate, expirationDate, createdBy, creationDate, updatedBy, updateDate)
select 1, catID, opID, riskLevel, onFlag, 
	'2000-01-01', '4000-01-01', 1, now(), 1, now()
from temp_pqfopmatrix_full;

insert into audit_category_rule (auditTypeID, catID, questionID, include, effectiveDate, expirationDate, createdBy, creationDate, updatedBy, updateDate)
select 2, c.categoryID, q.questionID, CASE WHEN m.id > 0 THEN 1 ELSE 0 END,
	'2000-01-01', '4000-01-01', 1, now(), 1, now()
from (select DISTINCT categoryID from desktopmatrix) c
join (select DISTINCT questionID from desktopmatrix) q
left join desktopmatrix m on m.categoryID = c.categoryID and m.questionID = q.questionID;

update audit_category_rule set 
level = (if(catID is null, 0, 1) + if(auditTypeID is null, 0, 1) +
	if(risk is null, 0, 1) + if(opID is null, 0, 1) +
	if(tagID is null, 0, 1) + if(questionID is null, 0, 1) +
	if(contractorType is null, 0, 1) + if(acceptsBids is null, 0, 1)),
priority = (if(catID is null, 0, 120) + if(auditTypeID is null, 0, 105) +
	if(risk is null, 0, 102) + if(opID is null, 0, 104) +
	if(questionID is null, 0, 125));

-- for orphaned accounts
-- delete from audit_category_rule where opID > 0 and opID not in (select id from accounts);

drop table temp_audit_type_rule;

create table temp_audit_type_rule as
select distinct ao.opID, ao.auditTypeID, ao.minRiskLevel, tagID
from accounts a
join operators o on a.id = o.id
join audit_operator ao on ao.canSee = 1 and ao.opID = o.inheritAudits -- o.inheritInsurance
join audit_type aType on aType.id = ao.auditTypeID and aType.classType != 'Policy'
where a.status in ('Active','Pending') and a.type = 'Operator';

TODO!! policy type conversion too

truncate table audit_type_rule;

insert into audit_type_rule (auditTypeID, opID, risk, tagID, include, effectiveDate, expirationDate, createdBy, creationDate, updatedBy, updateDate)
select a.auditTypeID, o.opID, r.risk, ao.tagID, CASE WHEN ao.opID is null then 0 else 1 end,
	'2000-01-01', '4000-01-01', 1, now(), 1, now()
from (select distinct auditTypeID from temp_audit_type_rule) a
join (select distinct opID from temp_audit_type_rule) o
join (select 1 risk union select 2 union select 3) r
left join temp_audit_type_rule ao on a.auditTypeID = ao.auditTypeID AND o.opID = ao.opID AND r.risk >= ao.minRiskLevel;

insert into audit_type_rule (auditTypeID, include, effectiveDate, expirationDate, createdBy, creationDate, updatedBy, updateDate)
select auditTypeID, case when sum(include) > 500 then 1 else 0 end,
	'2000-01-01', '4000-01-01', 1, now(), 1, now()
from audit_type_rule
group by auditTypeID;

update audit_type_rule set 
level = (if(auditTypeID is null, 0, 1) +
	if(risk is null, 0, 1) + if(opID is null, 0, 1) +
	if(tagID is null, 0, 1) + if(questionID is null, 0, 1) +
	if(contractorType is null, 0, 1) + if(acceptsBids is null, 0, 1)),
priority = (if(auditTypeID is null, 0, 105) +
	if(risk is null, 0, 102) + if(opID is null, 0, 104) +
	if(questionID is null, 0, 125));

